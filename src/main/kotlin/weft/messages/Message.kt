package weft.messages

sealed class Message {
	data class Update(val full: Boolean) : Message()
	data class Render(val full: Boolean) : Message()
	data class Logout(val now: Boolean) : Message()
	data class KeyPress (val rawKey: String, val shiftDown: Boolean = false, val controlDown: Boolean = false, val altDown: Boolean = false) : Message() {
		val key: KeyType = KeyType.fromString(rawKey)
	}

	fun process(username: String, token: String) {
		processors[username]?.forEach { p -> p.invoke(token, this) }
	}

	companion object {
		private val processors : MutableMap<String, MutableList<(String, Message)->Unit>> = mutableMapOf()

		fun register(username: String, processor: (String, Message)->Unit) {
			processors.getOrPut(username, { mutableListOf()}).add(processor)
		}

		fun unregister(username: String) {
			processors.remove(username)
		}
	}
}