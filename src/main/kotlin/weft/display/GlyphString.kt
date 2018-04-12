package weft.display


import java.awt.Color

data class GlyphString (val text: String = "", val foregroundColor: Color = Color.WHITE, val backgroundColor: Color = Color.BLACK){

    val length: Int
        get() { return text.length}

    operator fun get(index: Int) : Glyph {
        require(index >= 0) {"|index| $index must be >= 0"}
        require(index < text.length) {"|index| $index must be < ${text.length}"}
        return Glyph(text[index], foregroundColor, backgroundColor)
    }
}
