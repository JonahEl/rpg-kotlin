package weft.messages

enum class KeyType {
    Enter,
    Escape,
    Tab,
    Space,
    ArrowDown,
    ArrowLeft,
    ArrowRight,
    ArrowUp,
    End,
    Home,
    PageUp,
    PageDown,
    Clear,
    Num0,
    Num1,
    Num2,
    Num3,
    Num4,
    Num5,
    Num6,
    Num7,
    Num8,
    Num9,
    Raw;

    companion object {
        private val map = KeyType.values().associateBy { keyType: KeyType -> keyType.name }

        fun fromString(type: String) : KeyType {
            return when(type){
                " " -> Space
                "0" -> Num0
                "1" -> Num1
                "2" -> Num2
                "3" -> Num3
                "4" -> Num4
                "5" -> Num5
                "6" -> Num6
                "7" -> Num7
                "8" -> Num8
                "9" -> Num9
                else -> map[type] ?: Raw
            }
        }
    }
}
