package rpgserver.game.creatures.ai

import rpgserver.game.creatures.Creature
import rpgserver.game.creatures.Player
import rpgserver.game.map.TileLoc
import rpgserver.game.map.TileType

open class BaseAI(private val owner: Creature) {
	var target: Creature? = null

	open fun isFriendlyTo(other: Creature): Boolean {
		if (other == owner) return true
		return other !is Player
	}

	open fun onInteract(other: Creature) {
		if (!isFriendlyTo(other))
			owner.attack(other)
	}

	open fun onAttacked(attacker: Creature) {
		target = attacker

		val d = attacker.position.distance(owner.position)
		if (d <= 1)
			owner.attack(attacker)
	}

	open fun onEnter(loc: TileLoc, tile: TileType) {}

	open fun onUpdate() {
		if (target == null && owner.sightRange > 0)
			target = owner.map.creatures.nearby(owner.position, owner.sightRange).firstOrNull { c -> !isFriendlyTo(c) }

		val t = target ?: return
		if (t.isDead) {
			target = null
			return
		}

		if (t.position.distance(owner.position) <= owner.attackRange) {
			owner.attack(t)
		} else {
			owner.moveBy(owner.position.directionTo(t.position))
		}
	}
}