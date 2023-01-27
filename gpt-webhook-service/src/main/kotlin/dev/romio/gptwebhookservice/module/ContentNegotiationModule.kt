package dev.romio.gptwebhookservice.module

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.WhatsAppMessageSerializationAdapter
import dev.romio.gptwebhookservice.model.request.whatsapp.Message
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun Application.contentNegotiationModule(config: Config) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(Message::class.java, WhatsAppMessageSerializationAdapter())
        }
    }
}
