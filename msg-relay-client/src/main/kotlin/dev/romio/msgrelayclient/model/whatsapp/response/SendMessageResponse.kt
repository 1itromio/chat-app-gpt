package dev.romio.msgrelayclient.model.whatsapp.response


import com.google.gson.annotations.SerializedName

data class SendMessageResponse(
    @SerializedName("contacts")
    val contacts: List<Contact>,
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("messaging_product")
    val messagingProduct: String
)

data class Contact(
    @SerializedName("input")
    val input: String,
    @SerializedName("wa_id")
    val waId: String
)

data class Message(
    @SerializedName("id")
    val id: String
)