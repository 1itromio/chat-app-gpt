package dev.romio.gptwebhookservice

import dev.romio.gptengine.GptClient
import dev.romio.gptwebhookservice.config.ConfigImpl
import dev.romio.gptwebhookservice.config.TgBotMode
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.handler.createTelegramBot
import dev.romio.gptwebhookservice.module.callLoggingModule
import dev.romio.gptwebhookservice.module.contentNegotiationModule
import dev.romio.gptwebhookservice.module.requestValidationModule
import dev.romio.gptwebhookservice.module.routingModule
import dev.romio.gptwebhookservice.storage.InMemoryStorage
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    log.info("App Started, Creating configs and installing modules")
    val config = ConfigImpl(this)
    val gptClient = GptClient(config.openAiKey)
    val storage = InMemoryStorage(config)
    val conversationHandler = ConversationHandler(config, gptClient, storage)
    val tgBot = createTelegramBot(config, conversationHandler, log, storage, gptClient)
    // tgBot.setMyCommands()
    if (config.tgBotMode == TgBotMode.WEBHOOK) {
        log.info("Starting tg bot in webhook mode")
        tgBot.startWebhook()
    } else {
        log.info("Starting tg bot in polling mode")
        tgBot.startPolling()
    }
    callLoggingModule()
    contentNegotiationModule(config)
    routingModule(config, conversationHandler, onTelegramMessageReceived = {
        tgBot.processUpdate(it)
    })
    requestValidationModule(config)
}
