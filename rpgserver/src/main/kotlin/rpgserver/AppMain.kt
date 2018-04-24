package rpgserver

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.http.ClientAuth
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetServerOptions
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import rpg.common.Messages
import rpg.common.display.Glyph
import rpg.common.display.GlyphCell
import rpg.common.json.ColorSerialization
import rpg.common.json.InstantSerialization
import rpgserver.display.Display
import rpgserver.display.MessageScreen
import rpgserver.display.Screen

class TypedMessage<T>(private val classOfT: Class<T>, private val baseMessage: Message<JsonObject>) : Message<T> {
	override fun replyAddress(): String {
		return baseMessage.replyAddress()
	}

	override fun isSend(): Boolean {
		return baseMessage.isSend
	}

	override fun body(): T {
		return baseMessage.body().mapTo(classOfT)
	}

	override fun address(): String {
		return baseMessage.address()
	}

	override fun fail(failureCode: Int, message: String?) {
		return baseMessage.fail(failureCode, message)
	}

	override fun reply(message: Any?) {
		return baseMessage.reply(message)
	}

	override fun <R : Any?> reply(message: Any?, replyHandler: Handler<AsyncResult<Message<R>>>?) {
		return baseMessage.reply(message, replyHandler)
	}

	override fun reply(message: Any?, options: DeliveryOptions?) {
		return baseMessage.reply(message, options)
	}

	override fun <R : Any?> reply(message: Any?, options: DeliveryOptions?, replyHandler: Handler<AsyncResult<Message<R>>>?) {
		return baseMessage.reply(message, options, replyHandler)
	}

	override fun headers(): MultiMap {
		return baseMessage.headers()
	}
}

fun <T> EventBus.typedConsumer(address: String, classOfT: Class<T>, handler: (TypedMessage<T>) -> Unit = {}) {
	this.consumer<JsonObject>(address) { message ->
		handler(TypedMessage<T>(classOfT, message))
	}
}

fun EventBus.mapAndSend(address: String, data: Any) {
	println("Sending $data")
	this.send<String>(address, JsonObject.mapFrom(data)) { msg ->
		if (msg.failed())
			msg.cause().printStackTrace()
	}
}

fun <T> EventBus.mapAndSend(address: String, data: Any, classOfT: Class<T>, handler: (TypedMessage<T>) -> Unit) {
	this.send<JsonObject>(address, JsonObject.mapFrom(data)) { message ->
		handler(TypedMessage<T>(classOfT, message.result()))
	}
}

class AppMain {
	val vertx = Vertx.vertx()
	val display = Display()
	private var screen: Screen = MessageScreen(display, listOf("hi - server"))

	init {
		val bridge = TcpEventBusBridge.create(
				vertx,
				BridgeOptions()
						.addInboundPermitted(PermittedOptions().setAddress("keypress"))
						.addOutboundPermitted(PermittedOptions().setAddress("update")),
				NetServerOptions()
						.setClientAuth(ClientAuth.NONE)
		)

		val port = 9009
		val address = "localhost"
		bridge.listen(port, address, { res ->
			if (res.succeeded())
				println("Bridge started on $address:$port")
			else
				println("Bridge failed")
		})

		val eb = vertx.eventBus()

		ColorSerialization.register()
		InstantSerialization.register()

		var full = false
		eb.typedConsumer<Messages.KeyPress>("keypress", Messages.KeyPress::class.java) { message ->
			screen.processKeyPress(message.body())
			full = true
		}

		display.clear(Glyph(' '))
		screen.update()
		screen.render()

		vertx.setPeriodic(1000, { _ ->
			update(full).forEach {
				eb.mapAndSend("update", it)
			}
			full = false
		})
	}

	private fun update(forceFullRender: Boolean): List<GlyphCell> {
		val (next, changed) = screen.swapNextScreen()
		screen = next

		if (changed || forceFullRender)
			display.clear(Glyph(' '))

		screen.update()
		screen.render()

		return if (changed || forceFullRender)
			display.all(true)
		else
			display.changes(true)
	}

	private fun close() {
		vertx.close()
	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			val app = AppMain()
			println("Hit [Enter] to exit")
			readLine()

			app.close()
		}
	}
}
