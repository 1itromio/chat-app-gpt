package dev.romio.gptwebhookservice.handler

import arrow.core.Either
import arrow.core.getOrHandle
import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.BotMessage
import dev.romio.gptwebhookservice.model.UserMessage
import dev.romio.gptwebhookservice.model.UserMessageSource
import dev.romio.gptwebhookservice.model.request.whatsapp.Text
import dev.romio.gptwebhookservice.model.request.whatsapp.WhatsAppMessageRequest
import dev.romio.msgrelayclient.impl.WhatsAppMessageRelayClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger

class WhatsAppMessageHandler constructor(
    private val conversationHandler: ConversationHandler,
    private val messageRelayClient: WhatsAppMessageRelayClient,
    private val config: Config,
    private val log: Logger
) {
    suspend fun handleMessage(whatsAppMessageRequest: WhatsAppMessageRequest) {
        val whatsAppMessage = whatsAppMessageRequest.entry.lastOrNull()?.changes?.lastOrNull()?.value?.messages?.lastOrNull()
        val content = whatsAppMessage?.content
        if (content !is Text) {
            return
        }
        coroutineScope {
            launch {
                messageRelayClient.markMessageAsRead(config.whatsAppPhoneNumberId, whatsAppMessage.id)
                conversationHandler.getResponseMessage(
                    UserMessage(
                        msgId = whatsAppMessage.id,
                        userId = whatsAppMessage.from,
                        source = UserMessageSource.WHATS_APP,
                        msg = content.body
                    )
                ).let {
                    if (it.isRight()) {
                        val message = it.getOrHandle { BotMessage("Unknown") }.msg
                        val relayResult = messageRelayClient.sendTextMessage(
                            config.whatsAppPhoneNumberId,
                            whatsAppMessage.from,
                            false,
                            message
                        )
                        if (relayResult is Either.Left) {
                            log.error(
                                "Error occurred while relaying whatsapp message: " +
                                    "$message to: ${whatsAppMessage.from}, " +
                                    "phoneNumId: ${config.whatsAppPhoneNumberId}, " +
                                    "Error: ${relayResult.value.msg}"
                            )
                        }
                    } else {
                        log.error(
                            "Error occurred while getting response from ChatGpt, Error: " +
                                (it as Either.Left<dev.romio.gptengine.util.OpenAiClientError>).value.msg
                        )
                    }
                }
            }
        }
    }
}
