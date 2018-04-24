package rpgserver.display


import rpg.common.display.Glyph
import java.awt.Color

data class GlyphString(private val text: String = "", private val foregroundColor: Color = Color.WHITE, private val backgroundColor: Color = Color.BLACK) {

	val length: Int
		get() {
			return text.length
		}

	operator fun get(index: Int): Glyph {
		require(index >= 0) { "|index| $index must be >= 0" }
		require(index < text.length) { "|index| $index must be < ${text.length}" }
		return Glyph(text[index], foregroundColor, backgroundColor)
	}
}
