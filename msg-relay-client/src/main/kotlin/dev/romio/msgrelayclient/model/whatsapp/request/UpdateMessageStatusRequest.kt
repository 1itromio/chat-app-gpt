package dev.romio.msgrelayclient.model.whatsapp.request


import com.google.gson.annotations.SerializedName

data class UpdateMessageStatusRequest(
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("messaging_product")
    val messagingProduct: String = "whatsapp",
    @SerializedName("status")
    val status: String = "read"
)