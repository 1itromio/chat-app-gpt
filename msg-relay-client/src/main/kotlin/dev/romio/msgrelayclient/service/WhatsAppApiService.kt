package dev.romio.msgrelayclient.service

import dev.romio.msgrelayclient.model.whatsapp.request.SendMessageRequest
import dev.romio.msgrelayclient.model.whatsapp.request.UpdateMessageStatusRequest
import dev.romio.msgrelayclient.model.whatsapp.response.SendMessageResponse
import dev.romio.msgrelayclient.model.whatsapp.response.UpdateMessageStatusResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface WhatsAppApiService {

    @POST("/v15.0/{phoneNumberId}/messages")
    suspend fun sendMessage(
        @Path("phoneNumberId") phoneNumberId: String,
        @Body sendMessageRequest: SendMessageRequest
    ): SendMessageResponse

    @POST("/v15.0/{phoneNumberId}/messages")
    suspend fun updateMessageStatus(
        @Path("phoneNumberId") phoneNumberId: String,
        @Body updateMessageStatusRequest: UpdateMessageStatusRequest
    ): UpdateMessageStatusResponse
}