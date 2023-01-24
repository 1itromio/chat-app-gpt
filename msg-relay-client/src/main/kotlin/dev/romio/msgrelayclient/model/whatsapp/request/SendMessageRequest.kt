package dev.romio.msgrelayclient.model.whatsapp.request

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    val toPhoneNumber: String,
    val content: WhatsAppMessageContent,
    val messagingProduct: String = "whatsapp",
    val recipientType: String = "individual"
)

enum class WhatsAppMessageContentType(val type: String) {
    TEXT("text"),
    REACTION("reaction"),
    IMAGE("image"),
    UNKNOWN("unknown")
}

abstract class WhatsAppMessageContent(@Transient val type: WhatsAppMessageContentType)

data class WhatsAppTextMessage(
    @SerializedName("preview_url")
    val previewUrl: Boolean,
    @SerializedName("body")
    val body: String
): WhatsAppMessageContent(WhatsAppMessageContentType.TEXT)

data class WhatsAppImageMessage(
    @SerializedName("link")
    val link: String
): WhatsAppMessageContent(WhatsAppMessageContentType.IMAGE)

object WhatsAppUnknownMessage: WhatsAppMessageContent(WhatsAppMessageContentType.UNKNOWN)

