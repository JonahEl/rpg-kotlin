package weft.user

import org.apache.qpid.jms.JmsConnectionFactory
import org.apache.qpid.jms.message.JmsBytesMessage
import org.json.JSONObject
import weft.extensions.getBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.jms.*

class JMSConnection(clientReceiveQueue: String, clientSendQueue: String)
	: MessageListener, ExceptionListener {
	private val user: String
		get() = "admin"
	private val password: String
		get() = "password"
	private val host: String
		get() = "localhost"
	private val port: Int
		get() = 5672

	private val jmsConnection: Connection
	private val jmsSession: Session
	private val jmsProducer: MessageProducer
	private val jmsConsumer: MessageConsumer

	init {
		val factory = JmsConnectionFactory("amqp://$host:$port")

		jmsConnection = factory.createConnection(user, password)
		jmsConnection.start()

		jmsSession = jmsConnection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)

		jmsProducer = jmsSession.createProducer(jmsSession.createQueue(clientReceiveQueue))
		jmsProducer.deliveryMode = DeliveryMode.NON_PERSISTENT

		jmsConsumer = jmsSession.createConsumer(jmsSession.createQueue(clientSendQueue))
		jmsConsumer.messageListener = this
	}

	fun send(to: String, content: String) {
		val msg = jmsSession.createTextMessage( "\u0002\u0002\u0002$content")
		msg.setLongProperty("id", nextMessageID.incrementAndGet())
		msg.setStringProperty("to", to)
		jmsProducer.send(msg)
	}

	override fun onMessage(message: Message) {
		try {
			val body: String = when (message) {
				is JmsBytesMessage -> {
					val length = message.bodyLength.toInt()
					val b = ByteArray(length)
					message.readBytes(b, length)
					String(b, Charsets.UTF_8)
				}
				is TextMessage -> message.text
				else -> throw Exception("Unsupported message type")
			}

			val to = message.getStringProperty("to").toLowerCase()
			val token = message.getStringProperty("token")
			val action =  message.getStringProperty("action").toLowerCase()

			check(body.isNotBlank()) {"message body must not be blank"}
			check(to.isNotBlank()) {"|to| message property must not be blank"}
			check(token.isNotBlank()) {"|token| message property must not be blank"}
			check(action.isNotBlank()) {"|action| message property must not be blank"}

			println("message from $to : $body")

			val obj = JSONObject(body)
			when (action) {
				"render" -> weft.messages.Message.Render(obj.getBoolean("full", false))
				"key" -> weft.messages.Message.KeyPress(
						obj.getString("key"),
						obj.getBoolean("shiftKey", false),
						obj.getBoolean("ctrlKey", false),
						obj.getBoolean("altKey", false))
				"logout" -> weft.messages.Message.Logout(true)
				else -> throw Exception("unknown message type $action for $to")
			}.process(to, token)

		} catch (ex: Exception){
			println("error in OnMessage $ex")
		}
	}

	override fun onException(exception: JMSException) {
		println("error receiving message $exception")
	}

	fun close() {
		jmsProducer.send(jmsSession.createTextMessage("SHUTDOWN"))
		jmsConnection.close()
	}

	companion object {
		private val nextMessageID = AtomicLong(0L)
	}
}
