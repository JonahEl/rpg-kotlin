package rpgserver.game.creatures

import rpg.common.display.Glyph
import rpgserver.game.creatures.ai.FungusAI
import rpgserver.game.dice.D6
import rpgserver.game.map.Direction
import rpgserver.game.map.GameMap
import rpgserver.game.map.TileLoc
import java.awt.Color

class Fungus(map: GameMap) : Creature(map, Glyph('f', Color.GREEN), "Fungus") {
	init {
		ai = FungusAI(this)
		maxHealth = D6(4).roll()
		currentHealth = maxHealth
		attack = D6(1)
		sightRange = 0
		attackRange = 1
	}

	fun spread() {
		val loc = TileLoc(position).offset(Direction.random())
		if (position == loc)
			return
		map.creatures.addAt(Fungus(map), loc)
	}
}
