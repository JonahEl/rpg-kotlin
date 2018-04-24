package rpgserver.game.creatures.ai

import rpgserver.game.creatures.Creature
import rpgserver.game.creatures.Player
import rpgserver.game.map.MoveCost
import rpgserver.game.map.TileLoc
import rpgserver.game.map.TileType

class PlayerAI(private val owner: Player) : BaseAI(owner) {

	override fun isFriendlyTo(other: Creature): Boolean {
		return other == owner
	}

	override fun onAttacked(attacker: Creature) {
		target = attacker
	}

	override fun onEnter(loc: TileLoc, tile: TileType) {
		if (tile.moveCost == MoveCost.Open) {
			owner.position = loc
		} else if (tile.moveCost == MoveCost.Diggable) {
			owner.map.tiles.dig(loc)
		}
	}

	override fun onUpdate() {
		//if(target == null)
		target = owner.map.creatures.nearby(owner.position, 20).firstOrNull { c -> !isFriendlyTo(c) }

		val t = target ?: return
		if (t.isDead) {
			target = null
			return
		}
	}
}

