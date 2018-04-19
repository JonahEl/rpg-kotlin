package weft.game.creatures

import weft.display.Glyph
import weft.game.creatures.ai.BaseAI
import weft.game.dice.D0
import weft.game.dice.Dice
import weft.game.map.Direction
import weft.game.map.GameMap
import java.awt.Dimension
import java.awt.Point

open class Creature(map: GameMap, glyph: Glyph, name: String) : Entity(map, glyph, name) {
    var sightRange: Int = 1
    var attackRange: Int = 1
    var currentHealth: Int = 0
    var maxHealth: Int = 0
    var attack: Dice = D0()

    var ai: BaseAI = BaseAI(this)

    var isDead: Boolean = false
        get() = currentHealth <= 0

    override fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit) {
        val p = Point(position.x - scroll.x, position.y - scroll.y)
        if (p.x >= 0 && p.x < viewport.width && p.y >= 0 && p.y < viewport.height)
            draw(glyph, p)
    }

    override fun update() {
        ai.onUpdate()
    }

    fun moveBy(dir: Direction) {
        val newLoc = position.offset(dir)
        val other = map.creatures.at(newLoc)
        if (other == null)
            ai.onEnter(newLoc, map.tiles[newLoc])
        else
            ai.onInteract(other)
    }

    fun attack(other: Creature) {
        println("$this attacks $other for $attack")
        other.currentHealth -= attack.roll()

        if (other.isDead) {
            println("$other dies")
            map.creatures.remove(other)
        } else {
            other.ai.onAttacked(this)
            println("$other has ${other.currentHealth} health left")
        }
    }
}