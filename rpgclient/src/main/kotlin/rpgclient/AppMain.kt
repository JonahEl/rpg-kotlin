package rpgclient

import or.sebi.ebclient.EventBusClient
import rpg.common.Messages
import rpg.common.display.GlyphCell
import rpg.common.json.ColorSerialization
import rpg.common.json.InstantSerialization
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.time.Instant
import javax.swing.JFrame

class AppMain : JFrame(), KeyListener, WindowListener {
	private val renderer = Renderer()
	private val eventBus: EventBusClient

	init {
		add(renderer)
		pack()
		addWindowListener(this)
		addKeyListener(this)
		repaint()

		ColorSerialization.register()
		InstantSerialization.register()

		eventBus = EventBusClient("localhost", 9009) {
			println("Event error: ${it}")
		}

		eventBus.consumer<GlyphCell>("update") {
			println("update $it")
			renderer.apply(it)
		}

//		eventBus.consumer("update", listOf<GlyphCell>().javaClass) {
//			println("update message: $it")
//			renderer.applyChanges(it)
//		}
//
//		vertx.setPeriodic(10, { _ ->
//			repaint()
//		})
//
	}

	override fun keyPressed(e: KeyEvent) {
		println("sending key press")
		eventBus.send("keypress", Messages.KeyPress(Instant.now(), e.keyCode, e.isShiftDown, e.isControlDown, e.isAltDown)) {
			println("keypress reply $it")
		}
		repaint()
	}

	override fun keyReleased(e: KeyEvent) {}

	override fun keyTyped(e: KeyEvent) {}

	override fun windowDeiconified(e: WindowEvent?) {
	}

	override fun windowClosing(e: WindowEvent?) {
		eventBus.close()
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
