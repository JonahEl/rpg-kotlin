package rpgserver.game.creatures

import rpg.common.display.Glyph
import rpgserver.game.dice.D6
import rpgserver.game.map.GameMap
import java.awt.Color

class Spider(map: GameMap) : Creature(map, Glyph('s', Color.RED), "Spider") {

	init {
		maxHealth = D6(10).roll()
		currentHealth = maxHealth
		attack = D6(4)
		sightRange = 10
		attackRange = 1
	}
}
