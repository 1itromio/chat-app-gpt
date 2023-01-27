package dev.romio.gptwebhookservice.handler.telegram

import dev.romio.gptengine.GptClient
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.model.UserMessageSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class CommandHandler private constructor(
    private val conversationHandler: ConversationHandler
) : TelegramMessageHandler() {

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        var conversationHandler: ConversationHandler? = null

        fun build(): CommandHandler {
            if (conversationHandler == null) {
                throw IllegalArgumentException("All required arguments are not provided for the CommandHandler Builder")
            }
            return CommandHandler(conversationHandler!!)
        }
    }

    override fun handle(telegramMessage: TelegramReceivedMessage): Flow<TelegramResponseMessage> {
        if (telegramMessage is TelegramReceivedTextMessage && telegramMessage.text.startsWith("/")) {
            return handleCommand(telegramMessage)
        }
        return next?.handle(telegramMessage) ?: flowOf(getDefaultTextMessage(telegramMessage))
    }

    private fun handleCommand(telegramMessage: TelegramReceivedTextMessage): Flow<TelegramResponseMessage> = flow {
        val responseText = when (telegramMessage.text) {
            "start" -> "Hi there!!!"
            "clear" -> {
                conversationHandler.clearConversation(UserMessageSource.TELEGRAM, telegramMessage.chatId.toString())
                "All your previous chats are cleared"
            }
            else -> "You have entered an invalid command"
        }
        emit(TelegramResponseTextMessage(telegramMessage.chatId, responseText))
    }
}
