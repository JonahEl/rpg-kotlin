package weft.game.creatures.ai

import weft.game.creatures.Fungus

class FungusAI (private val owner: Fungus) : BaseAI(owner) {

    override fun onUpdate() {
        super.onUpdate()

        val nearby = owner.map.creatures.nearby(owner.position, 1);
        if (nearby.size < 5 && Math.random() < 0.001)
            owner.spread()
        }
    }

