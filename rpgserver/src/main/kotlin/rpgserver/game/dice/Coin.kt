package rpgserver.game.dice

class Coin : Dice(2, 1) {

	companion object {
		fun flip(): Boolean {
			return Dice.randGenerator.nextBoolean()
		}
	}

	override fun roll(): Int {
		return if (flip()) 1 else 0
	}

	override fun toString(): String {
		return "${count}D$size"
	}

}