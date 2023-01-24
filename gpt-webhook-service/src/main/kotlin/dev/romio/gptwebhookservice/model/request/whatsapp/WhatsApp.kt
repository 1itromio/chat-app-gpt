package dev.romio.gptwebhookservice.model.request.whatsapp

import com.google.gson.annotations.SerializedName

data class WhatsAppMessageRequest(
    @SerializedName("entry")
    val entry: List<Entry>,
    @SerializedName("object")
    val objectX: String
)

data class Entry(
    @SerializedName("changes")
    val changes: List<Change>,
    @SerializedName("id")
    val id: String
)

data class Change(
    @SerializedName("field")
    val fieldType: String,
    @SerializedName("value")
    val value: Value
)

data class Value(
    @SerializedName("contacts")
    val contacts: List<Contact>,
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("messaging_product")
    val messagingProduct: String,
    @SerializedName("metadata")
    val metadata: Metadata
)

data class Contact(
    @SerializedName("profile")
    val profile: Profile,
    @SerializedName("wa_id")
    val waId: String
)

data class Metadata(
    @SerializedName("display_phone_number")
    val displayPhoneNumber: String,
    @SerializedName("phone_number_id")
    val phoneNumberId: String
)

data class Profile(
    @SerializedName("name")
    val name: String
)

data class Message(
    val from: String,
    val id: String,
    val content: WhatsAppMessage,
    val timestamp: Long,
    val type: String
)

abstract class WhatsAppMessage(val type: String)

data class Text(
    @SerializedName("body")
    val body: String
): WhatsAppMessage("text")

object UnknownMessage: WhatsAppMessage("unknown")