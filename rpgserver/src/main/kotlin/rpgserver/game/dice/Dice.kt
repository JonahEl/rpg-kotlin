package rpgserver.game.dice

import java.util.*

abstract class Dice(val size: Int, val count: Int) {

	open fun roll(): Int {
		if (size <= 0 || count <= 0)
			return 0

		var result = 0
		for (t in 0..count)
			result += random(1, size)
		return result
	}

	override fun toString(): String {
		return "${count}D$size"
	}

	companion object {
		val randGenerator: Random = Random()

		fun random(min: Int, max: Int): Int {
			require(min >= 0) { "|min| [$min] must be >= 0" }
			require(min < max) { "|min| [$min] must be < |max| [$max]" }
			return randGenerator.nextInt((max - min) + 1) + min
		}
	}
}