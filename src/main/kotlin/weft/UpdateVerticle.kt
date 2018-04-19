package weft

import weft.display.*

class UpdateVerticle : io.vertx.core.AbstractVerticle() {

	val display = Display()
	private var screen: Screen = MessageScreen(display, listOf("hi"))

	override fun start() {
		println("[Worker] Starting in ${java.lang.Thread.currentThread().name}")

		display.clear(Glyph(' '))
		screen.update()
		screen.render()

		vertx.eventBus().consumer<Messages.Update>("update", { message ->
			message.reply(update(message.body()))
		})

		vertx.eventBus().consumer<Messages.KeyPress>("keypress", { message ->
			screen.processKeyPress(message.body())
		})
	}

	private fun update(msg: Messages.Update): List<GlyphCell> {
		val (next, changed) = screen.swapNextScreen()
		screen = next

		if (changed || msg.forceFullRender)
			display.clear(Glyph(' '))

		screen.update()
		screen.render()

		return if (changed || msg.forceFullRender)
			display.all(true)
		else
			display.changes(true)
	}
}