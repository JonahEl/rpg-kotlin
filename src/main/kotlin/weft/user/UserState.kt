package weft.user

import org.json.JSONObject
import weft.display.Display
import weft.display.MessageScreen
import weft.display.Screen
import weft.messages.Message
import weft.render.RenderCell
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class UserState private constructor(val username: String) {
	val display: Display = Display()
	var screen: Screen = MessageScreen(display, listOf("hi"))
	val token: String
		get() = "xXx${username}xXx"
	val queueReceive: String
		get() = "game.$username.receive"
	val queueSend: String
		get() = "game.$username.send"
	val queueUrl: String
		get() = "ws://localhost:61614/stomp"
	val queueUser: String
		get() = "user"
	val queuePassword: String
		get() = ""

	private val jms: JMSConnection = JMSConnection(queueReceive, queueSend)

	init {
		require(username.isNotBlank()) { "|username| can not be blank" }

		println("initializing $username")
		screen.render()
		screen.update()
		screen.render()
		Message.register(username, this::process)
	}

	private fun process(msgToken: String, msg: weft.messages.Message) {
		if (msgToken != token)
			return

		when (msg) {
			is Message.Render -> sendUpdate(msg.full)
			is Message.Logout -> {
				jms.close()
				Message.unregister(username)
				users.remove(username)
				println("user $username logged out")
			}
			is Message.KeyPress -> screen.processKeyPress(msg)
		}
	}

	private fun sendUpdate(forceFullRender: Boolean = false) {
		val (next, changed) = screen.swapNextScreen()
		screen = next
		screen.update()
		screen.render()

		val full = changed || forceFullRender;

		val cells = if (full)
			display.all(true).map { c -> RenderCell(c) }
		else
			display.changes(true).map { c -> RenderCell(c) }

		if (cells.isEmpty())
			return

		var msg = JSONObject()

		if (full)
			msg = msg.put("restart", true)
					.put("width", display.widthInCharacters)
					.put("height", display.heightInCharacters)

		msg = msg.put("cells", cells)

		jms.send(username, msg.toString())
	}

	companion object {
		private val users = ConcurrentHashMap<String, UserState>()
		private var updateThread: Thread? = null

		fun login(username: String, password: String): UserState {
			if (password != "123") throw Exception("Bad password. Try 123")
			val us = users.getOrPut(username, { UserState(username) })
			println("user $username logged in")
			return us
		}

		fun startUpdates() {
			updateThread = thread(true, true) {
				Thread.sleep(1000)

				while (!Thread.currentThread().isInterrupted) {
					users.forEach(10) { _, u -> u.sendUpdate(false) }
					try {
						Thread.sleep(100)
					}catch (ex: InterruptedException) {
						break
					}
				}
			}
		}

		fun stopUpdates() {
			updateThread?.interrupt()
		}
	}
}

