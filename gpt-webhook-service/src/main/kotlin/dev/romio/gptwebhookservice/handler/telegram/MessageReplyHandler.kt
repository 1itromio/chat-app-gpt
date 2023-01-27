package dev.romio.gptwebhookservice.handler.telegram

import arrow.core.Either
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import dev.romio.gptwebhookservice.handler.ConversationHandler
import dev.romio.gptwebhookservice.model.UserMessage
import dev.romio.gptwebhookservice.model.UserMessageSource
import dev.romio.gptwebhookservice.util.replaceLast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.Logger

class MessageReplyHandler private constructor(
    private val conversationHandler: ConversationHandler,
    private val tgBot: Bot,
    private val log: Logger?
) : TelegramMessageHandler() {

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        var conversationHandler: ConversationHandler? = null
        var tgBot: Bot? = null
        var log: Logger? = null

        fun build(): MessageReplyHandler {
            if (conversationHandler == null || tgBot == null) {
                throw IllegalArgumentException(
                    "All must required arguments are not provided for " +
                        "MessageReplyHandler Builder"
                )
            }
            return MessageReplyHandler(conversationHandler!!, tgBot!!, log)
        }
    }

    override fun handle(telegramMessage: TelegramReceivedMessage): Flow<TelegramResponseMessage> {
        if (telegramMessage is TelegramReceivedTextMessage) {
            val userMessage = UserMessage(
                telegramMessage.msgId.toString(),
                telegramMessage.chatId.toString(),
                UserMessageSource.TELEGRAM,
                telegramMessage.text
            )

            return flow {
                tgBot.sendChatAction(ChatId.fromId(telegramMessage.chatId), ChatAction.TYPING)
                when (val response = conversationHandler.getResponseMessage(userMessage)) {
                    is Either.Right -> handleResponse(
                        telegramMessage,
                        telegramMessage.chatId,
                        response.value.msg,
                        this,
                        tgBot
                    )
                    is Either.Left -> {
                        log?.info(response.value.msg)
                        emit(
                            TelegramResponseTextMessage(
                                telegramMessage.chatId,
                                "Something went wrong while processing your request"
                            )
                        )
                    }
                }
            }
        }
        return next?.handle(telegramMessage) ?: flowOf(getDefaultTextMessage(telegramMessage))
    }

    private suspend fun handleResponse(
        receivedMessage: TelegramReceivedTextMessage,
        chatId: Long,
        responseText: String,
        flowCollector: FlowCollector<TelegramResponseMessage>,
        tgBot: Bot
    ) {
        log?.info("Received Response: $responseText")
        if (responseText.contains("command-text:")) {
            val responseTextSplit = responseText.split("command-text:")
            val finalResponseText = responseTextSplit[0].replaceLast(",", "").trim()
            flowCollector.emit(TelegramResponseTextMessage(chatId, finalResponseText))
            if (responseTextSplit.size > 1) {
                responseTextSplit[1].split(",").firstOrNull()?.trim()?.also {
                    handleCommand(receivedMessage, chatId, it, flowCollector, tgBot)
                }
            }
        } else {
            flowCollector.emit(
                TelegramResponseTextMessage(
                    chatId,
                    responseText
                )
            )
        }
    }

    private suspend fun handleCommand(
        receivedMessage: TelegramReceivedTextMessage,
        chatId: Long,
        command: String,
        flowCollector: FlowCollector<TelegramResponseMessage>,
        tgBot: Bot
    ) {
        when (command) {
            "generate-image" -> {
                tgBot.sendChatAction(ChatId.fromId(chatId), ChatAction.TYPING)
                when (val imageGenerationResult = conversationHandler.getImage(receivedMessage.text)) {
                    is Either.Right -> {
                        flowCollector.emit(
                            TelegramResponsePhotoMessage(
                                chatId,
                                TelegramFile.ByUrl(imageGenerationResult.value)
                            )
                        )
                    }
                    else -> {
                        flowCollector.emit(
                            TelegramResponseTextMessage(
                                chatId,
                                "I am sorry, Image generation failed"
                            )
                        )
                    }
                }
            }
            "generate-code" -> {
                tgBot.sendChatAction(ChatId.fromId(chatId), ChatAction.TYPING)
                val replyText = when (
                    val codeGenerationResult =
                        conversationHandler.generateCode(receivedMessage.text)
                ) {
                    is Either.Right -> {
                        codeGenerationResult.value
                    }
                    else -> "I am sorry, Code generation failed"
                }
                flowCollector.emit(TelegramResponseTextMessage(chatId, replyText))
            }
        }
    }
}
