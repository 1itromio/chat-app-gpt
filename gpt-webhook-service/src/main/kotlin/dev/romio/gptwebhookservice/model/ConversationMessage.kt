package dev.romio.gptwebhookservice.model

abstract class ConversationMessage(
    val msg: String,
    val userType: UserType
)

class UserMessage(
    val msgId: String,
    val userId: String,
    val source: UserMessageSource,
    msg: String
): ConversationMessage(msg, UserType.USER)

class BotMessage(
    msg: String
): ConversationMessage(msg, UserType.BOT)

enum class UserType {
    BOT, USER
}

enum class UserMessageSource {
    WHATS_APP, TELEGRAM
}

class Conversation(
    val userMessage: UserMessage,
    val botMessage: BotMessage
)