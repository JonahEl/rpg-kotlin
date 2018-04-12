package weft.game.creatures

import weft.display.Glyph
import weft.game.dice.D6
import weft.game.map.GameMap
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
