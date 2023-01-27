package dev.romio.gptwebhookservice.module

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.WhatsAppMessageSerializationAdapter
import dev.romio.gptwebhookservice.model.request.whatsapp.Message
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.contentNegotiationModule(config: Config) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(Message::class.java, WhatsAppMessageSerializationAdapter())
        }
    }
}
