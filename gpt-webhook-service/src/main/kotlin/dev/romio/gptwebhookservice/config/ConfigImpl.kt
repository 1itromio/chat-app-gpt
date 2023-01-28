package dev.romio.gptwebhookservice.config

import io.ktor.server.application.Application

class ConfigImpl(private val application: Application) : Config {

    private val appConfig by lazy {
        application.environment.config
    }

    override val whatsAppPhoneNumberId: String
        get() = appConfig.property("config.whatsApp.phoneNumberId").getString()
    override val whatsAppApiKey: String
        get() = appConfig.property("config.whatsApp.apiKey").getString()
    override val whatsAppVerifyToken: String
        get() = appConfig.property("config.whatsApp.verifyToken").getString()
    override val openAiKey: String
        get() = appConfig.property("config.openAi.apiKey").getString()
    override val maxConversationSize: Int
        get() = appConfig.property("config.maxConversationSize").getString().toInt()
    override val textModel: String
        get() = appConfig.property("config.openAi.model.text").getString()
    override val codeModel: String
        get() = appConfig.property("config.openAi.model.code").getString()
    override val startingPrompt: String
        get() = appConfig.property("config.openAi.startingPrompt").getString()
    override val configPassword: String
        get() = appConfig.property("config.configPassword").getString()
    override val tgBotToken: String
        get() = appConfig.property("config.telegram.botToken").getString()
    override val tgBotMode: TgBotMode
        get() = TgBotMode.valueOf(appConfig.property("config.telegram.mode").getString())
    override val domain: String
        get() = appConfig.property("config.domain").getString()
    override val defaultUserId: String
        get() = appConfig.property("config.telegram.defaultUserId").getString()
}
