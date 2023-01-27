package dev.romio.gptwebhookservice.handler.telegram

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class UserFilterHandler private constructor(
    private val isValidUserId: suspend (Long?) -> Boolean,
    private val defaultMessage: TelegramResponseMessage,
) : TelegramMessageHandler() {

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        var isValidUserId: (suspend (Long?) -> Boolean)? = null
        var defaultMessage: TelegramResponseMessage? = null

        fun build(): UserFilterHandler {
            if (isValidUserId == null || defaultMessage == null) {
                throw IllegalArgumentException("All Required are not set for the builder")
            }
            return UserFilterHandler(isValidUserId!!, defaultMessage!!)
        }
    }

    override fun handle(telegramMessage: TelegramReceivedMessage): Flow<TelegramResponseMessage> = flow {
        if (isValidUserId(telegramMessage.userId) && next != null) {
            (next?.handle(telegramMessage) ?: flowOf(defaultMessage)).collect {
                emit(it)
            }
        } else {
            emit(defaultMessage)
        }
    }
}
