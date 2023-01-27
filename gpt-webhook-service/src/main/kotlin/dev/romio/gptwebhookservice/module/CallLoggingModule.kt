package dev.romio.gptwebhookservice.module

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.callLoggingModule() {
    install(CallLogging) {
        level = Level.INFO
    }
}
