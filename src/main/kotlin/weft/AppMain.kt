package weft

import weft.render.WebApi
import weft.user.UserState
import kotlin.concurrent.thread

class AppMain() {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			println("Starting")

			Runtime.getRuntime().addShutdownHook(thread(false, false) {
				println("shutdown")
			})

			UserState.startUpdates()
			WebApi.start()
			println("Started")

			println("Hit 'Enter' to exit")
			println(readLine())

			println("stopping")
			WebApi.stop()
			UserState.stopUpdates();
			println("stopped")
		}
	}
}
