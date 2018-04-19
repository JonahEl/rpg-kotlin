package weft

import java.time.Instant

sealed class Messages {
	data class Update(val at: Instant, val forceFullRender: Boolean) : Messages()
	data class KeyPress(val at: Instant, val key: Int, val shiftDown: Boolean = false, val controlDown: Boolean = false, val altDown: Boolean = false) : Messages()
}


