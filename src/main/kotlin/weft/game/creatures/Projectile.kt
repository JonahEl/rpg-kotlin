package weft.game.creatures

import weft.display.Glyph
import weft.game.dice.Dice
import weft.game.map.*
import java.awt.Dimension
import java.awt.Point

class Projectile(map: GameMap, glyph: Glyph, name: String, val damage: Dice, val range: Int, val targetTile: TileLoc)  : Entity(map, glyph, name) {
    var isDead: Boolean = false
    var distanceTravelled: Int = 0
        set(value) {
            field = value
            if (field > range)
                isDead = true
        }

    override fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit) {
        draw(glyph, Point(position.x - scroll.x, position.y - scroll.y))
    }

    override fun update() {
        moveBy(position.directionTo(targetTile))

        if(isDead)
            map.projectiles.remove(this)
    }

    fun moveBy(dir: Direction) {
        val newLoc = position.offset(dir);
        val other = map.creatures.at(newLoc)
        if (other == null) {
            if (map.tiles[newLoc].moveCost == MoveCost.Open) {
                position = newLoc
                distanceTravelled++
            } else {
                distanceTravelled = range + 1
            }
        } else
            attack(other)
    }

    fun attack(other: Creature) {
        println("$this attacks ${other} for $damage")
        other.currentHealth -= damage.roll()

        if (other.isDead) {
            println("${other} dies")
            map.creatures.remove(other)
        } else {
            println("${other} has ${other.currentHealth} health left")
        }

        isDead = true
    }
}