package dev.romio.gptwebhookservice.module

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.model.response.config.ConfigResponse
import dev.romio.gptwebhookservice.model.response.health.HealthResponse
import dev.romio.gptwebhookservice.route.whatsApp
import dev.romio.gptwebhookservice.storage.InMemoryStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.routingModule(config: Config) {
    install(Routing) {
        webhooks(config)
        health()
        config(config)
    }
}

fun Route.webhooks(config: Config) {
    val conversationHandler = ConversationHandler(config, InMemoryStorage(config))
    route("/webhook") {
        whatsApp(config, conversationHandler)
    }
}

fun Route.health() {
    route("/health") {
        get {
            call.respond(HealthResponse("ok"))
        }
    }
}

fun Route.config(config: Config) {
    route("/config") {
        get {
            val password = call.request.queryParameters["password"]
            if(password == config.configPassword) {
                call.respond(ConfigResponse(
                    maxConversationSize = config.maxConversationSize,
                    whatsAppPhoneNumId = config.whatsAppPhoneNumberId,
                    whatsAppVerifyToken = config.whatsAppVerifyToken,
                    whatsAppApiKey = config.whatsAppApiKey,
                    openAiApiKey = config.openAiKey,
                    startingPrompt = config.startingPrompt,
                    textModel = config.textModel,
                    codeModel = config.codeModel
                ))
            } else {
                call.respondText("Please provide password in query parameter", status = HttpStatusCode.BadRequest)
            }
        }
    }
}
