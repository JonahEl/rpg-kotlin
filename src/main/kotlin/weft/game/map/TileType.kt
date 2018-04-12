package weft.game.map

import weft.display.Glyph
import java.awt.Color

enum class TileType constructor(val glyph: Glyph, val moveCost: MoveCost) {
    Floor(Glyph(0x301C, Color(0x292e84)), MoveCost.Open),
    DirtWall(Glyph('#', Color(150, 150, 0)), MoveCost.Diggable),
    StoneWall(Glyph('#', Color(150, 150, 150)), MoveCost.Blocked),
    Bound(Glyph('x', Color(150, 150, 150)), MoveCost.Blocked);
}