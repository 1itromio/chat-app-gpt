package dev.romio.gptwebhookservice.route

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.handler.WhatsAppMessageHandler
import dev.romio.gptwebhookservice.model.request.whatsapp.WhatsAppMessageRequest
import dev.romio.msgrelayclient.impl.WhatsAppMessageRelayClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.whatsApp(config: Config, conversationHandler: ConversationHandler) {
    val whatsAppMessageRelayClient = WhatsAppMessageRelayClient(config.whatsAppApiKey)
    val whatsAppMessageHandler = WhatsAppMessageHandler(
        conversationHandler,
        whatsAppMessageRelayClient,
        config,
        this.application.log
    )
    get("/whatsapp/receive") {
        val receivedVerifyToken = call.request.queryParameters["hub.verify_token"]
        val mode = call.request.queryParameters["hub.mode"]
        val challenge = call.request.queryParameters["hub.challenge"] ?: ""
        if(config.whatsAppVerifyToken == receivedVerifyToken && mode == "subscribe") {
            call.respondText(challenge)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/whatsapp/receive") {
        val payload = call.receive<WhatsAppMessageRequest>()
        whatsAppMessageHandler.handleMessage(payload)
        call.respondText("Done")
    }
}