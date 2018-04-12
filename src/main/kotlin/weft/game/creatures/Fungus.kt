package weft.game.creatures

import weft.display.Glyph
import weft.game.creatures.ai.FungusAI
import weft.game.dice.D6
import weft.game.map.Direction
import weft.game.map.GameMap
import weft.game.map.MoveCost
import weft.game.map.TileLoc
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
        if(position == loc)
            return
        map.creatures.addAt(Fungus(map), loc)
    }
}
