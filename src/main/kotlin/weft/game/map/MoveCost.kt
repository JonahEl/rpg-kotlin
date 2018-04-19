package weft.game.map

enum class MoveCost(val cost: Int, val blocking: Boolean, val diggable: Boolean) {
    Open(1, false, false),
    Blocked(1, true, false),
    Diggable(1, true, true);
}