package dev.romio.gptwebhookservice.module

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.model.response.config.ConfigResponse
import dev.romio.gptwebhookservice.model.response.health.HealthResponse
import dev.romio.gptwebhookservice.route.telegram
import dev.romio.gptwebhookservice.route.whatsApp
import dev.romio.gptwebhookservice.storage.Storage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Application.routingModule(
    config: Config,
    conversationHandler: ConversationHandler,
    storage: Storage,
    onTelegramMessageReceived: (String) -> Unit
) {
    install(Routing) {
        webhooks(config, conversationHandler, onTelegramMessageReceived)
        health()
        config(config, storage)
    }
}

fun Route.webhooks(
    config: Config,
    conversationHandler: ConversationHandler,
    onTelegramMessageReceived: (String) -> Unit
) {
    route("/webhook") {
        route("/whatsApp") {
            whatsApp(config, conversationHandler)
        }
        route("/telegram") {
            telegram(onTelegramMessageReceived)
        }
    }
}

fun Route.health() {
    route("/health") {
        get {
            call.respond(HealthResponse("ok"))
        }
    }
}

fun Route.config(config: Config, storage: Storage) {
    route("/config") {
        get {
            val password = call.request.queryParameters["password"]
            if (password == config.configPassword) {
                call.respond(
                    ConfigResponse(
                        maxConversationSize = config.maxConversationSize,
                        whatsAppPhoneNumId = config.whatsAppPhoneNumberId,
                        whatsAppVerifyToken = config.whatsAppVerifyToken,
                        whatsAppApiKey = config.whatsAppApiKey,
                        openAiApiKey = config.openAiKey,
                        startingPrompt = config.startingPrompt,
                        textModel = config.textModel,
                        codeModel = config.codeModel,
                        tgBotToken = config.tgBotToken,
                        tgBotMode = config.tgBotMode.name,
                        domain = config.domain
                    )
                )
            } else {
                call.respondText("Please provide password in query parameter", status = HttpStatusCode.BadRequest)
            }
        }
        post("/add-users") {
            val userIds = call.receive<List<String>>()
            storage.addUsers(userIds)
            call.respondText("User Ids are added successfully")
        }
    }

}
