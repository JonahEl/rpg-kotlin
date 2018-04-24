package rpgserver.game.creatures

import rpg.common.display.Glyph
import rpgserver.game.dice.Dice
import rpgserver.game.map.Direction
import rpgserver.game.map.GameMap
import rpgserver.game.map.MoveCost
import rpgserver.game.map.TileLoc
import java.awt.Dimension
import java.awt.Point

class Projectile(map: GameMap, glyph: Glyph, name: String, private val damage: Dice, private val range: Int, private val targetTile: TileLoc) : Entity(map, glyph, name) {
	private var isDead: Boolean = false
	private var distanceTravelled: Int = 0
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

		if (isDead)
			map.projectiles.remove(this)
	}

	private fun moveBy(dir: Direction) {
		val newLoc = position.offset(dir)
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

	private fun attack(other: Creature) {
		println("$this attacks $other for $damage")
		other.currentHealth -= damage.roll()

		if (other.isDead) {
			println("$other dies")
			map.creatures.remove(other)
		} else {
			println("$other has ${other.currentHealth} health left")
		}

		isDead = true
	}
}