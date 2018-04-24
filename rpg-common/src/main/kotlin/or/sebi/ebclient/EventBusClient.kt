package or.sebi.ebclient

import io.vertx.core.json.Json
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.*

/**
 * Created by sblanc on 3/12/17.
 * Kotlin conversion of https://github.com/sebastienblanc/vertx-java-tcp-eventbus-bridge
 */
class EventBusClient(host: String, port: Int, private val errorHandler: (String) -> Unit) {
	private val clientSocket = Socket(host, port)
	private val outToServer = DataOutputStream(clientSocket.getOutputStream())
	private val inFromServer = DataInputStream(clientSocket.getInputStream())
	private val handlers = mutableMapOf<String, (InternalMessage) -> Unit>()
	private var exiting = false

	private val inThread: Thread = object : Thread() {
		override fun run() {
			try {
				while (!exiting) {
					val count = inFromServer.readInt()
					val buffer = ByteArray(count)
					inFromServer.readFully(buffer)
					val jsonString = String(buffer)
					println(jsonString)
					val message = Json.decodeValue(jsonString, InternalMessage::class.java)
					if (message.isError)
						errorHandler(message.message)
					else
						handlers[message.address]?.invoke(message)
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	init {
		inThread.start()
	}

	fun close() {
		exiting = true
		for (i in 0..100) {
			if (!inThread.isAlive)
				break
			Thread.sleep(10)
		}
	}

	private fun <T> convert(body: Any): T {
		println("convert $body")
		return body as T
	}

	fun <T> send(address: String, body: T, handler: (T) -> Unit = {}) {
		val msg = InternalMessage(type = "send", address = address, body = body)
		handlers[msg.replyAddress] = { if (it.body != null) handler(convert(it.body)) }
		sendFrame(msg)
	}

//	fun publish(message: Message) {
//		sendFrame(message.copy(type = "publish"))
//	}

	fun <T> consumer(address: String, handler: (T) -> Unit = {}) {
		handlers[address] = { if (it.body != null) handler(convert(it.body)) }
	}

	private fun sendFrame(message: InternalMessage) {
		val jsonString = Json.encode(message)
		println("Send $jsonString")
		outToServer.writeInt(jsonString.toByteArray().size)
		outToServer.write(jsonString.toByteArray())
	}

	private data class InternalMessage(val type: String, val address: String, val body: Any?, val headers: Map<String, String> = mutableMapOf<String, String>(), val replyAddress: String = UUID.randomUUID().toString()) {
		var message: String = ""

		val isError: Boolean
			get() = "err" == type
		/* {"type":"err","address":"b71511ae-acea-47a0-be8e-a39951e89264","sourceAddress":"keypress","failureCode":-1,"failureType":"TIMEOUT","message":"Timed out after waiting 30000(ms) for a reply. address: 1, repliedAddress: keypress"}*/
	}
}
