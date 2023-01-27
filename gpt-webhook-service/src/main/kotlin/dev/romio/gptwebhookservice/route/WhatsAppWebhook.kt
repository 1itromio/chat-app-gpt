package dev.romio.gptwebhookservice.route

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.handler.WhatsAppMessageHandler
import dev.romio.gptwebhookservice.model.request.whatsapp.WhatsAppMessageRequest
import dev.romio.msgrelayclient.impl.WhatsAppMessageRelayClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.whatsApp(config: Config, conversationHandler: ConversationHandler) {
    val whatsAppMessageRelayClient = WhatsAppMessageRelayClient(config.whatsAppApiKey)
    val whatsAppMessageHandler = WhatsAppMessageHandler(
        conversationHandler,
        whatsAppMessageRelayClient,
        config,
        this.application.log
    )
    get("/receive") {
        val receivedVerifyToken = call.request.queryParameters["hub.verify_token"]
        val mode = call.request.queryParameters["hub.mode"]
        val challenge = call.request.queryParameters["hub.challenge"] ?: ""
        if (config.whatsAppVerifyToken == receivedVerifyToken && mode == "subscribe") {
            call.respondText(challenge)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/receive") {
        val payload = call.receive<WhatsAppMessageRequest>()
        whatsAppMessageHandler.handleMessage(payload)
        call.respondText("Done")
    }
}
