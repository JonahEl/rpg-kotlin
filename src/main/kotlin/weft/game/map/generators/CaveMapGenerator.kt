package weft.game.map.generators

import weft.game.creatures.Creature
import weft.game.creatures.Fungus
import weft.game.creatures.Spider
import weft.game.dice.Coin
import weft.game.map.GameMap
import weft.game.map.TileType


class CaveMapGenerator(width: Int, height: Int) : MapGenerator(width, height) {
    init {
        initTiles { if (Math.random() < 0.6) TileType.Floor else TileType.StoneWall }
        smooth(7)
        markEdges()
    }

    override fun initCreatures(map: GameMap): List<Creature> {
        val creatures: MutableList<Creature> = mutableListOf()
        for (i in 0..7)
            creatures.add(Fungus(map))
        creatures.add(Spider(map))
        return creatures.toList()
    }

    private fun smooth(times: Int) {
        val tiles2 = Array(width) { Array(height, { TileType.Floor }) }
        for (time in 0 until times) {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    var floors = 0
                    var rocks = 0

                    for (ox in -1..1) {
                        for (oy in -1..1) {
                            if (x + ox < 0 || x + ox >= width || y + oy < 0
                                    || y + oy >= height)
                                continue

                            if (tiles[x + ox][y + oy] === TileType.Floor)
                                floors++
                            else
                                rocks++
                        }
                    }
                    tiles2[x][y] = if (floors >= rocks)
                            TileType.Floor
                            else if(Coin.flip()) TileType.DirtWall
                            else TileType.StoneWall
                }
            }
            tiles = tiles2
        }
    }
}