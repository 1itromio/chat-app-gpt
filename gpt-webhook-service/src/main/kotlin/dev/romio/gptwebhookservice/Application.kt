package dev.romio.gptwebhookservice

import dev.romio.gptwebhookservice.config.ConfigImpl
import dev.romio.gptwebhookservice.module.callLoggingModule
import dev.romio.gptwebhookservice.module.contentNegotiationModule
import dev.romio.gptwebhookservice.module.requestValidationModule
import dev.romio.gptwebhookservice.module.routingModule
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    log.info("App Started, Creating configs and installing modules")
    val config = ConfigImpl(this)
    callLoggingModule()
    contentNegotiationModule(config)
    routingModule(config)
    requestValidationModule(config)
}