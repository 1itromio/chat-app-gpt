package dev.romio.gptwebhookservice.route

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.telegram(onMessageReceived: (String) -> Unit) {
    post("/receive") {
        val receivedBody = call.receiveText()
        onMessageReceived(receivedBody)
    }
}
