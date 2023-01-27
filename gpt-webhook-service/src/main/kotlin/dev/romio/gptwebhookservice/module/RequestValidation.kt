package dev.romio.gptwebhookservice.module

import dev.romio.gptwebhookservice.config.Config
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.requestValidationModule(config: Config) {
    install(RequestValidation) {
    }
}
