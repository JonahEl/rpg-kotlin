package rpgserver.game.creatures

import rpg.common.Messages
import rpg.common.display.Glyph
import rpgserver.game.creatures.ai.PlayerAI
import rpgserver.game.dice.D20
import rpgserver.game.dice.D6
import rpgserver.game.map.Direction
import rpgserver.game.map.GameMap
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.event.KeyEvent

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

	fun processKeyPress(key: Messages.KeyPress): Boolean {
		when (key.key) {
			KeyEvent.VK_NUMPAD9,
			KeyEvent.VK_PAGE_UP -> moveBy(Direction.NE)
			KeyEvent.VK_NUMPAD8,
			KeyEvent.VK_UP -> moveBy(Direction.N)
			KeyEvent.VK_NUMPAD7,
			KeyEvent.VK_HOME -> moveBy(Direction.NW)
			KeyEvent.VK_NUMPAD6,
			KeyEvent.VK_RIGHT -> moveBy(Direction.E)
			KeyEvent.VK_NUMPAD5,
			KeyEvent.VK_CLEAR -> rest()
			KeyEvent.VK_SPACE -> fire()
			KeyEvent.VK_NUMPAD4,
			KeyEvent.VK_LEFT -> moveBy(Direction.W)
			KeyEvent.VK_NUMPAD3,
			KeyEvent.VK_PAGE_DOWN -> moveBy(Direction.SE)
			KeyEvent.VK_NUMPAD2,
			KeyEvent.VK_DOWN -> moveBy(Direction.S)
			KeyEvent.VK_NUMPAD1,
			KeyEvent.VK_END -> moveBy(Direction.SW)
			else -> return false
		}

		return true
	}
}