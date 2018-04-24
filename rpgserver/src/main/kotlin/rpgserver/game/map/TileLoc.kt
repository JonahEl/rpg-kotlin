package rpgserver.game.map

import rpgserver.game.dice.Dice
import java.awt.Dimension
import kotlin.math.roundToInt

data class TileLoc(val x: Int, val y: Int) {
	constructor(loc: TileLoc) : this(loc.x, loc.y)

	fun distance(target: TileLoc): Int {
		val dx = Math.abs(x - target.x)
		val dy = Math.abs(y - target.y)
		return (D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy)).roundToInt()
	}

	fun offset(dir: Direction): TileLoc {
		return TileLoc(this.x + dir.x(), this.y + dir.y())
	}

	fun isIn(dim: Dimension): Boolean {
		return x >= 0 && x < dim.width && y >= 0 && y < dim.height
	}

	fun directionTo(other: TileLoc): Direction {
		return Direction.normalize(other.x - x, other.y - y)
	}

	companion object {
		const val D: Int = 1
		val D2: Double = Math.sqrt(2.0)

		fun random(dim: Dimension): TileLoc {
			return random(0, dim.width, 0, dim.height)
		}

		private fun random(minX: Int, maxX: Int, minY: Int, maxY: Int): TileLoc {
			return TileLoc(Dice.random(minX, maxX), Dice.random(minY, maxY))
		}
	}
}