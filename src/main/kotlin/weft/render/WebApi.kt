package weft.render

import io.javalin.Javalin
import org.json.JSONObject
import weft.user.UserState

object WebApi {

	private var javalin: Javalin? = null

	fun start() {
		javalin = Javalin.create().apply {
			port(7070)
			enableStaticFiles("public")
			get("/hello") { context -> context.result("hello") }
			post("/login") { context ->
				run {
					val (username, password) = context.mapFormParams("username", "password")
							?: throw Exception("Missing form params")
					val us = UserState.login(username.toLowerCase(), password)
					context.result(
							JSONObject()
									.put("username", us.username)
									.put("token", us.token)
									.put("queueUrl", us.queueUrl)
									.put("queueReceive", us.queueReceive)
									.put("queueSend", us.queueSend)
									.put("queueUser", us.queueUser)
									.put("queuePassword", us.queuePassword)
									.toString()
					)
				}
			}
		}.start()
	}

	fun stop() {
		javalin?.stop()
	}
}
