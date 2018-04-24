package rpgserver.game.map.generators

import rpgserver.game.creatures.Creature
import rpgserver.game.map.GameMap
import rpgserver.game.map.TileType


abstract class MapGenerator(protected val width: Int, protected val height: Int) {
	var tiles: Array<Array<TileType>> = Array(width) { Array(height, { TileType.Floor }) }
		protected set(value) {
			require(value.size == width) { "|tiles.size| [${value.size}] must equal |width| $width" }
			require(value[0].size == height) { "|tiles[0].size| [${value[0].size}] must equal |height| $height" }
			field = value
		}

	init {
		require(width >= 3) { "|width| [$width] must be > 3" }
		require(height >= 3) { "|height| [$height] must be > 3" }
	}

	open fun initCreatures(map: GameMap): List<Creature> {
		return listOf()
	}

	protected fun initTiles(getTile: () -> TileType) {
		for (x in 0 until width) {
			for (y in 0 until height) {
				tiles[x][y] = getTile()
			}
		}
	}

	protected fun markEdges() {
		for (x in 0 until width) {
			tiles[x][0] = TileType.Bound
			tiles[x][height - 1] = TileType.Bound
		}
		for (y in 0 until height) {
			tiles[0][y] = TileType.Bound
			tiles[width - 1][y] = TileType.Bound
		}
	}

}