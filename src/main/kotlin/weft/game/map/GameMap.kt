package weft.game.map

import weft.display.Glyph
import weft.game.creatures.Creature
import weft.game.creatures.EntityList
import weft.game.creatures.Player
import weft.game.creatures.Projectile
import weft.game.map.generators.MapGenerator
import java.awt.Dimension
import java.awt.Point

class GameMap(generator: MapGenerator) {
    var turn: Long = 0
        private set(value) {
            require(value > field) { "Long |value| [$value] must be greater that |turn| [$field] " }
            field = value
        }

    val tiles: TileList = TileList(generator.tiles.clone())
    val size: Dimension

    var player: Player = Player(this)
    val creatures: EntityList<Creature> = EntityList(true, tiles::blocking)
    val projectiles: EntityList<Projectile> = EntityList(false, tiles::blocking)

    init {
        size = tiles.dimensions
        generator.initCreatures(this).forEach { c -> creatures.addAt(c, findEmptyLocation()) }
        player.position = findEmptyLocation()
    }

    private tailrec fun findEmptyLocation(): TileLoc {
        val loc = tiles.randomNonBlocking()
        return if(!creatures.occupied(loc)) loc
        else findEmptyLocation()
    }

    fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit) {
        tiles.render(viewport, scroll, draw)
        creatures.render(viewport, scroll, draw)
        projectiles.render(viewport, scroll, draw)
        player.render(viewport, scroll, draw)
    }

    fun update() {
        player.update()
        creatures.update()
        projectiles.update()

        turn += 1
    }
}