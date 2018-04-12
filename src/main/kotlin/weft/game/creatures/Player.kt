package weft.game.creatures

import weft.display.Glyph
import weft.game.creatures.ai.PlayerAI
import weft.game.dice.D20
import weft.game.dice.D6
import weft.game.map.Direction
import weft.game.map.GameMap
import weft.messages.KeyType
import weft.messages.Message
import java.awt.Color
import java.awt.Dimension
import java.awt.Point

class Player(map: GameMap) : Creature(map, Glyph(0x26F5, Color.WHITE), "Player") {

	init {
		ai = PlayerAI(this)
		maxHealth = D20(10).roll()
		currentHealth = maxHealth
		attack = D6(2)
		sightRange = 20
		attackRange = 10
	}

	override fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit) {
		val t = ai.target
		if (t != null) {
			val d = position.directionTo(t.position)
			val dp = position.offset(d)
			draw(Glyph('◦', glyph.foregroundColor), Point(dp.x - scroll.x, dp.y - scroll.y))
		}

		draw(glyph, Point(position.x - scroll.x, position.y - scroll.y))
	}

	private fun rest() {
		if (currentHealth < maxHealth)
			currentHealth++
	}

	private fun fire() {
		val t = ai.target ?: return
		println("firing at $t")
		map.projectiles.addAt(Projectile(map, Glyph('◦', Color.RED), "Projectile", attack, attackRange, t.position), position)
	}

	fun processKeyPress(key: Message.KeyPress): Boolean {
		when (key.key) {
			KeyType.Num9,
			KeyType.PageUp -> moveBy(Direction.NE);
			KeyType.Num8,
			KeyType.ArrowUp -> moveBy(Direction.N);
			KeyType.Num7,
			KeyType.Home -> moveBy(Direction.NW);
			KeyType.Num6,
			KeyType.ArrowRight -> moveBy(Direction.E)
			KeyType.Num5,
			KeyType.Clear -> rest()
			KeyType.Space -> fire()
			KeyType.Num4,
			KeyType.ArrowLeft -> moveBy(Direction.W)
			KeyType.Num3,
			KeyType.PageDown -> moveBy(Direction.SE)
			KeyType.Num2,
			KeyType.ArrowDown -> moveBy(Direction.S)
			KeyType.Num1,
			KeyType.End -> moveBy(Direction.SW)
			else -> return false
		}

		return true
	}
}