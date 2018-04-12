package weft.game.map

import weft.display.Glyph
import java.awt.Dimension
import java.awt.Point

class TileList(private val tiles: Array<Array<TileType>>) {

    val dimensions: Dimension
        get() = Dimension(tiles.size, tiles[0].size)

    operator fun get(loc: TileLoc): TileType {
        return if (!loc.isIn(dimensions)) TileType.Bound
        else tiles[loc.x][loc.y]
    }

    operator fun set(loc: TileLoc, tile: TileType) {
        if (!loc.isIn(dimensions)) return
        tiles[loc.x][loc.y] = tile
    }

    tailrec fun randomNonBlocking(): TileLoc {
        val loc = TileLoc.random(dimensions);
        return if (!blocking(loc)) loc
        else randomNonBlocking()
    }

    fun blocking(loc: TileLoc) : Boolean {
        return this[loc].moveCost.blocking
    }

    fun dig(loc: TileLoc) {
        if (this[loc].moveCost.diggable)
            this[loc] = TileType.Floor
    }

    fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit) {
        for (x in 0 until viewport.width) {
            for (y in 0 until viewport.height) {
                val w = TileLoc(x + scroll.x, y + scroll.y)
                draw(this[w].glyph, Point(x, y))
            }
        }
    }
}