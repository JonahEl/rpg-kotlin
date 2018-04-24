package rpgserver.display

import rpg.common.Messages
import java.awt.event.KeyEvent

class MessageScreen(terminal: Display, private var lines: List<String>) : Screen(terminal) {

	override fun render() {
		val start = (display.heightInCharacters / 2) + (lines.size / 2)
		lines.forEachIndexed { i, s -> display.writeCenter(s, start + i) }
		display.writeCenter("-- press [enter] to play --", display.heightInCharacters - 2)
		display.writeCenter("-- press [escape] to exit --", display.heightInCharacters - 1)
	}

	override fun processKeyPress(key: Messages.KeyPress) {
		nextScreen = when (key.key) {
			KeyEvent.VK_ENTER -> PlayScreen(display)
			KeyEvent.VK_ESCAPE -> MessageScreen(display, listOf("exiting"))
			else -> nextScreen
		}
	}
}
