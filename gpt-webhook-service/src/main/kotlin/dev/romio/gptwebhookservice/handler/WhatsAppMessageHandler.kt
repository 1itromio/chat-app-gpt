package dev.romio.gptwebhookservice.handler

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

class WhatsAppMessageHandler constructor(
    private val conversationHandler: ConversationHandler,
    private val messageRelayClient: WhatsAppMessageRelayClient,
    private val config: Config
) {
    suspend fun handleMessage(whatsAppMessageRequest: WhatsAppMessageRequest)  {
        val whatsAppMessage = whatsAppMessageRequest.entry.lastOrNull()?.changes?.lastOrNull()?.value?.messages?.lastOrNull()
        val content = whatsAppMessage?.content
        if(content !is Text) {
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
                ).collect {
                    if(it.isRight()) {
                        messageRelayClient.sendTextMessage(
                            config.whatsAppPhoneNumberId,
                            whatsAppMessage.from,
                            false,
                            it.getOrHandle { BotMessage("Unknown") }.msg
                        )
                    }
                }
            }
        }
    }
}