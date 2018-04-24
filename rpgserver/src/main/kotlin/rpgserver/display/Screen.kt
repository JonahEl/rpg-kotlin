package rpgserver.display

import rpg.common.Messages
import rpg.common.display.Glyph
import java.awt.Dimension

abstract class Screen(val display: Display) {
	val screenSize: Dimension = Dimension(display.widthInCharacters, display.heightInCharacters)
	var nextScreen: Screen? = null

	init {
		display.clear(Glyph(' '))
	}

	fun swapNextScreen(): Swap {
		val ns = nextScreen
		return if (ns == null)
			Swap(this, false)
		else
			Swap(ns, true)
	}

	open fun render() {}

	open fun update() {}

	open fun processKeyPress(key: Messages.KeyPress) {}

	data class Swap(val newScreen: Screen, val changed: Boolean)
}

