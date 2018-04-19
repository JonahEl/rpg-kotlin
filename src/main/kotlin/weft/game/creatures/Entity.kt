package weft.game.creatures

import weft.display.Glyph
import weft.game.map.GameMap
import weft.game.map.TileLoc
import java.awt.Dimension
import java.awt.Point
import java.util.concurrent.atomic.AtomicLong

abstract class Entity(var map: GameMap, var glyph: Glyph, val name: String) {
    val id: Long = nextID()
    var position: TileLoc = TileLoc(0, 0)

    abstract fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit)
    abstract fun update()

    companion object {
        private var idTracker: AtomicLong = AtomicLong(0)
        fun nextID(): Long {
            return idTracker.incrementAndGet()
        }
    }
}