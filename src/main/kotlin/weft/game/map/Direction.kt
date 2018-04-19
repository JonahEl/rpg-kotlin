package weft.game.map

import weft.game.dice.Dice

enum class Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    fun x() : Int {
        return when (this) {
            Direction.NE -> 1
            Direction.N -> 0
            Direction.NW -> -1

            Direction.E -> 1
            Direction.W -> -1

            Direction.SE -> 1
            Direction.S -> 0
            Direction.SW -> -1
        }
    }

    fun y() : Int {
        return when (this) {
            Direction.NE -> -1
            Direction.N -> -1
            Direction.NW -> -1

            Direction.E -> 0
            Direction.W -> 0

            Direction.SE -> 1
            Direction.S -> 1
            Direction.SW -> 1
        }
    }

    companion object {
        fun random() : Direction {
            return values()[Dice.random(0, 7)]
        }

        fun normalize(x : Int, y : Int) : Direction {
            val m = when {
                x == 0 -> Math.abs(y)
                y == 0 -> Math.abs(x)
                else -> Math.max(Math.abs(x), Math.abs(y))
            }
            val a = x / m.toDouble()
            val b = y / m.toDouble()

            return when {
                b < -0.333 -> when {
                    a > 0.333 -> Direction.NE
                    a < -0.333 -> Direction.NW
                    else -> Direction.N
                }
                b > 0.333 -> when {
                    a > 0.333 -> Direction.SE
                    a < -0.333 -> Direction.SW
                    else -> Direction.S
                }
                else -> when {
                    a > 0 -> Direction.E
                    else -> Direction.W
                }
            }
        }
    }
}