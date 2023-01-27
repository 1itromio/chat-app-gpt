package dev.romio.gptwebhookservice.handler

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.webhook
import dev.romio.gptengine.GptClient
import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.config.TgBotMode
import dev.romio.gptwebhookservice.handler.telegram.*
import dev.romio.gptwebhookservice.storage.Storage
import dev.romio.gptwebhookservice.util.perform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.slf4j.Logger

fun createTelegramBot(
    config: Config,
    botConversationHandler: ConversationHandler,
    logger: Logger,
    storage: Storage,
    chatGptClient: GptClient
): Bot {
    return bot {
        token = config.tgBotToken
        if(config.tgBotMode == TgBotMode.WEBHOOK) {
            webhook {
                url = "${config.domain}/webhook/telegram/receive"
            }
        }
        dispatch {
            telegramError {
                logger.error(error.getErrorMessage())
            }
            text {
                logger.info("Received text: $text, User Id: ${message.from?.id}")
                handleMessage {
                    addHandler {
                        commandHandler {
                            conversationHandler = botConversationHandler
                            gptClient = chatGptClient
                        }
                    }
                    addHandler {
                        userFilterHandler {
                            isValidUserId = {
                                storage.isValidUser(it.toString())
                            }
                            defaultMessage = TelegramResponseTextMessage(message.chat.id, text)
                        }
                    }
                    addHandler {
                        messageReplyHandler {
                            tgBot = bot
                            conversationHandler = botConversationHandler
                            log = logger
                        }
                    }
                }?.perform(CoroutineScope(Dispatchers.Default)) {
                    when(it) {
                        is TelegramResponseTextMessage -> bot.sendMessage(ChatId.fromId(it.chatId), it.text)
                    }
                }
            }
        }
    }
}

fun TextHandlerEnvironment.handleMessage(
    init: TelegramMessageHandler.Builder.() -> Unit
): Flow<TelegramResponseMessage>? {
    val builder = TelegramMessageHandler.Builder()
    builder.init()
    return builder.tgMessageHandler?.handle(TelegramReceivedTextMessage(
        userId = this.message.from?.id,
        msgId = this.message.messageId,
        chatId = this.message.chat.id,
        text = this.text
    ))
}

