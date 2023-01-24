package dev.romio.msgrelayclient.model.whatsapp.response


import com.google.gson.annotations.SerializedName
import dev.romio.msgrelayclient.error.RelayClientError

data class WhatsAppErrorWrapper(
    @SerializedName("error")
    val error: WhatsAppError
)

data class WhatsAppError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("error_data")
    val errorData: ErrorData,
    @SerializedName("error_subcode")
    val errorSubcode: Int,
    @SerializedName("fbtrace_id")
    val fbtraceId: String,
    @SerializedName("message")
    val message: String?,
    @SerializedName("type")
    val type: String?
): RelayClientError(message ?: "")

data class ErrorData(
    @SerializedName("details")
    val details: String?,
    @SerializedName("messaging_product")
    val messagingProduct: String
)