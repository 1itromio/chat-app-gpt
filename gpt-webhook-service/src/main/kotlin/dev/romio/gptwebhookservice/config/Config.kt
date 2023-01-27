package dev.romio.gptwebhookservice.config

interface Config {
    val whatsAppPhoneNumberId: String
    val whatsAppApiKey: String
    val whatsAppVerifyToken: String
    val openAiKey: String
    val maxConversationSize: Int
    val textModel: String
    val codeModel: String
    val startingPrompt: String
    val configPassword: String
    val tgBotToken: String
    val tgBotMode: TgBotMode
    val domain: String
}