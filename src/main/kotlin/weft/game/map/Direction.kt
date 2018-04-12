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
        when(this) {
            Direction.NE -> return 1
            Direction.N -> return 0
            Direction.NW -> return -1

            Direction.E -> return 1
            Direction.W -> return -1

            Direction.SE -> return 1
            Direction.S -> return 0
            Direction.SW -> return -1
        }
    }

    fun y() : Int {
        when(this) {
            Direction.NE -> return -1
            Direction.N -> return -1
            Direction.NW -> return -1

            Direction.E -> return 0
            Direction.W -> return 0

            Direction.SE -> return 1
            Direction.S -> return 1
            Direction.SW -> return 1
        }
    }

    companion object {
        fun random() : Direction {
            return values().get(Dice.random(0, 7))
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