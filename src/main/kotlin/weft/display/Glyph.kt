package weft.display


import java.awt.Color

data class Glyph (val character: Int, val foregroundColor: Color = Color.WHITE, val backgroundColor: Color = Color.BLACK){
    var dirty: Boolean = true

	constructor(character: Char, foregroundColor: Color = Color.WHITE, backgroundColor: Color = Color.BLACK) : this(character.toInt(), foregroundColor, backgroundColor)
}
