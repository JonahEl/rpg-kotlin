package weft.game.map.generators

import weft.game.map.TileType


class EmptyMapGenerator(width: Int = 3, height: Int = 3) : MapGenerator(width, height) {
    init {
        initTiles { TileType.Floor }
        markEdges()
    }
}