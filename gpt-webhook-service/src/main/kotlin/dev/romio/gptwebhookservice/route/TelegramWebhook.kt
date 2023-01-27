package dev.romio.gptwebhookservice.route

import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.telegram(onMessageReceived: (String) -> Unit) {
    post("/receive") {
        val receivedBody = call.receiveText()
        onMessageReceived(receivedBody)
    }
}
