package weft.render

import weft.display.GlyphCell
import java.awt.Color

data class RenderCell(val x: Int, val y: Int, val glyph: Int, val foreColor: String, val backColor: String) {

    constructor(cell : GlyphCell) : this(cell.x, cell.y, cell.glyph.character, glyphColorToHtmlColor(cell.glyph.foregroundColor), glyphColorToHtmlColor(cell.glyph.backgroundColor))

    companion object {
        fun glyphColorToHtmlColor(color: Color): String {
            return "#%02x%02x%02x".format(color.red, color.green, color.blue);
        }
    }
}
