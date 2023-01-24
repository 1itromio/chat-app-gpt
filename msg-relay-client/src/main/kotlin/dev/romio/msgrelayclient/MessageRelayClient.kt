package dev.romio.msgrelayclient

import arrow.core.Either
import dev.romio.msgrelayclient.error.RelayError
import dev.romio.msgrelayclient.model.whatsapp.response.SendMessageResponse
import dev.romio.msgrelayclient.model.whatsapp.response.UpdateMessageStatusResponse

interface MessageRelayClient {
    suspend fun markMessageAsRead(
        phoneNumberId: String,
        messageId: String
    ): Either<RelayError, UpdateMessageStatusResponse>

    suspend fun sendTextMessage(
        phoneNumberId: String,
        toPhoneNumber: String,
        previewUrl: Boolean,
        msg: String
    ): Either<RelayError, SendMessageResponse>
}