package dev.romio.gptwebhookservice.route

import com.github.kotlintelegrambot.Bot
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.telegram(onMessageReceived: (String) -> Unit) {
    post("/receive") {
        val receivedBody = call.receiveText()
        onMessageReceived(receivedBody)
    }
}