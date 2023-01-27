package dev.romio.gptwebhookservice.handler.telegram

import kotlinx.coroutines.flow.Flow

abstract class TelegramMessageHandler {

    class Builder {
        var tgMessageHandler: TelegramMessageHandler? = null
            private set

        fun addHandler(handlerBuilder: () -> TelegramMessageHandler) {
            if (this.tgMessageHandler == null) {
                this.tgMessageHandler = handlerBuilder()
            } else {
                this.tgMessageHandler?.next = handlerBuilder()
            }
        }
    }

    protected var next: TelegramMessageHandler? = null
    abstract fun handle(telegramMessage: TelegramReceivedMessage): Flow<TelegramResponseMessage>

    protected fun getDefaultTextMessage(telegramMessage: TelegramReceivedMessage): TelegramResponseMessage {
        return TelegramResponseTextMessage(
            telegramMessage.chatId,
            "Unknown"
        )
    }
}

fun commandHandler(init: CommandHandler.Builder.() -> Unit) = CommandHandler.Builder(init).build()
fun userFilterHandler(init: UserFilterHandler.Builder.() -> Unit) = UserFilterHandler.Builder(init).build()
fun messageReplyHandler(init: MessageReplyHandler.Builder.() -> Unit) = MessageReplyHandler.Builder(init).build()
