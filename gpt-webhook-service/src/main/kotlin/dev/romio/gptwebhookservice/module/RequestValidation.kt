package dev.romio.gptwebhookservice.module

import dev.romio.gptwebhookservice.config.Config
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation

fun Application.requestValidationModule(config: Config) {
    install(RequestValidation) {
    }
}
