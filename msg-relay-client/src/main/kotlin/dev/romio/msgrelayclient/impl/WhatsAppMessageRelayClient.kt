package dev.romio.msgrelayclient.impl

import arrow.core.Either
import com.google.gson.GsonBuilder
import dev.romio.msgrelayclient.MessageRelayClient
import dev.romio.msgrelayclient.error.RelayError
import dev.romio.msgrelayclient.model.SendMessageRequestSerializationAdapter
import dev.romio.msgrelayclient.model.whatsapp.request.SendMessageRequest
import dev.romio.msgrelayclient.model.whatsapp.request.UpdateMessageStatusRequest
import dev.romio.msgrelayclient.model.whatsapp.request.WhatsAppMessageContent
import dev.romio.msgrelayclient.model.whatsapp.request.WhatsAppTextMessage
import dev.romio.msgrelayclient.model.whatsapp.response.SendMessageResponse
import dev.romio.msgrelayclient.model.whatsapp.response.UpdateMessageStatusResponse
import dev.romio.msgrelayclient.model.whatsapp.response.WhatsAppErrorWrapper
import dev.romio.msgrelayclient.service.WhatsAppApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class WhatsAppMessageRelayClient(private val whatsAppApiKey: String): MessageRelayClient {

    companion object {
        private const val BASE_URL = "https://graph.facebook.com"
        private const val HEADER_AUTH = "Authorization"
    }

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(SendMessageRequest::class.java, SendMessageRequestSerializationAdapter())
            .create()
    }

    private val whatsAppApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor {
                val newRequest = it.request()
                    .newBuilder()
                    .addHeader(HEADER_AUTH, "Bearer $whatsAppApiKey")
                    .build()
                it.proceed(newRequest)
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(WhatsAppApiService::class.java)
    }

    override suspend fun markMessageAsRead(
        phoneNumberId: String,
        messageId: String
    ): Either<RelayError, UpdateMessageStatusResponse> = delegateResponseHandling {
        whatsAppApiService.updateMessageStatus(
            phoneNumberId,
            UpdateMessageStatusRequest(messageId = messageId)
        )
    }

    override suspend fun sendTextMessage(
        phoneNumberId: String,
        toPhoneNumber: String,
        previewUrl: Boolean,
        msg: String
    ): Either<RelayError, SendMessageResponse> = delegateResponseHandling {
        val sendMessageRequest = SendMessageRequest(toPhoneNumber, WhatsAppTextMessage(previewUrl, msg))
        whatsAppApiService.sendMessage(phoneNumberId, sendMessageRequest)
    }

    private suspend fun <T> delegateResponseHandling(serviceCall: suspend () -> T): Either<RelayError, T> {
        return try {
            Either.Right(serviceCall())
        } catch (ex: Exception) {
            val error = when(ex) {
                is IOException -> RelayError.NetworkError
                is HttpException -> kotlin.runCatching {
                    ex.response()?.errorBody()?.string()?.let {
                        val result = gson.fromJson(it, WhatsAppErrorWrapper::class.java)
                        RelayError.ClientError(ex.response()?.code() ?: 400, result.error)
                    } ?: RelayError.UnknownError(ex.message())
                }.getOrDefault(RelayError.HttpError(ex.response()?.code() ?: 400, ex.message()))
                else -> {
                    RelayError.UnknownError(ex.message ?: ex.localizedMessage ?: "")
                }
            }
            Either.Left(error)
        }
    }

}