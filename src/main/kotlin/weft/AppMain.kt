package weft

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import weft.display.GlyphCell
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.time.Instant
import javax.swing.JFrame


class AppMain : JFrame(), KeyListener, WindowListener {
	private val renderer = Renderer()
	private val vertx = Vertx.vertx()!!

	init {
		add(renderer)
		pack()
		addWindowListener(this)
		addKeyListener(this)
		repaint()

		println("[Main] Running in ${java.lang.Thread.currentThread().name}")
		MessageCodecs.register(vertx.eventBus())

		vertx.deployVerticle("weft.UpdateVerticle", DeploymentOptions().setWorker(true))

		vertx.setPeriodic(100, { _ ->
			vertx.eventBus().send<List<GlyphCell>>("update", Messages.Update(Instant.now(), false), { r ->
				renderer.applyChanges(r.result().body())
			})
		})

		vertx.setPeriodic(10, { _ ->
			repaint()
		})
	}

	override fun keyPressed(e: KeyEvent) {
		vertx.eventBus().send<Any>("keypress", Messages.KeyPress(Instant.now(), e.keyCode, e.isShiftDown, e.isControlDown, e.isAltDown), { _ -> })

//        screen = screen.respondToUserInput(e)
//        if (screen is ExitScreen ) {
//            dispose()
//            return
//        }
		repaint()
	}

	override fun keyReleased(e: KeyEvent) {}

	override fun keyTyped(e: KeyEvent) {}

	override fun windowDeiconified(e: WindowEvent?) {
	}

	override fun windowClosing(e: WindowEvent?) {
		vertx.close()
	}

	override fun windowActivated(e: WindowEvent?) {
	}

	override fun windowDeactivated(e: WindowEvent?) {
	}

	override fun windowOpened(e: WindowEvent?) {
	}

	override fun windowIconified(e: WindowEvent?) {
	}

	override fun windowClosed(e: WindowEvent?) {
	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			val app = AppMain()
			app.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			app.isVisible = true
		}
	}
}
