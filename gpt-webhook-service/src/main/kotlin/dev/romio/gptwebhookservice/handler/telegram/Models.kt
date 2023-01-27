package dev.romio.gptwebhookservice.handler.telegram

import com.github.kotlintelegrambot.entities.TelegramFile

enum class TelegramMessageType {
    TEXT
}

abstract class TelegramReceivedMessage(
    val userId: Long?,
    val msgId: Long,
    val chatId: Long,
    val type: TelegramMessageType
)

class TelegramReceivedTextMessage(
    userId: Long?,
    msgId: Long,
    chatId: Long,
    val text: String
) : TelegramReceivedMessage(userId, msgId, chatId, TelegramMessageType.TEXT)

abstract class TelegramResponseMessage(
    val chatId: Long
)

class TelegramResponseTextMessage(
    chatId: Long,
    val text: String
) : TelegramResponseMessage(chatId)

class TelegramResponsePhotoMessage(
    chatId: Long,
    val tgFile: TelegramFile
) : TelegramResponseMessage(chatId)
