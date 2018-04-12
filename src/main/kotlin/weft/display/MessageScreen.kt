package weft.display

import weft.messages.KeyType
import weft.messages.Message

class MessageScreen(terminal: Display, var lines: List<String>) : Screen(terminal) {

	override fun render() {
		val start = (display.heightInCharacters / 2) + (lines.size / 2);
		lines.forEachIndexed { i, s -> display.writeCenter(s, start + i) }
		display.writeCenter("-- press [enter] to play --", display.heightInCharacters - 2)
		display.writeCenter("-- press [escape] to exit --", display.heightInCharacters - 1)
	}

	override fun processKeyPress(key: Message.KeyPress) {
		nextScreen = when {
			key.key == KeyType.Enter -> PlayScreen(display)
			key.key == KeyType.Escape -> MessageScreen(display, listOf("exiting"))
			else -> nextScreen
		}
	}
}
