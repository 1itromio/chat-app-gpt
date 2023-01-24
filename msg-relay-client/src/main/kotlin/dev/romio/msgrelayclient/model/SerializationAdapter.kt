package dev.romio.msgrelayclient.model

import com.google.gson.*
import dev.romio.msgrelayclient.model.whatsapp.request.*
import java.lang.reflect.Type

class SendMessageRequestSerializationAdapter: JsonSerializer<SendMessageRequest>, JsonDeserializer<SendMessageRequest> {

    private val gson by lazy {
        GsonBuilder().create()
    }

    override fun serialize(
        src: SendMessageRequest?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if(src == null) {
            throw IllegalArgumentException("Provided source for WhatsAppMessageSerialization is null")
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("messaging_product", src.messagingProduct)
        jsonObject.addProperty("recipient_type", src.recipientType)
        jsonObject.addProperty("to", src.toPhoneNumber)
        jsonObject.addProperty("type", src.content.type.type)
        jsonObject.addProperty(src.content.type.type, gson.toJson(src.content))
        return jsonObject
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SendMessageRequest {
        if(json == null) {
            throw JsonParseException("Invalid or null WhatsApp Message object passed for deserialization")
        }
        val jsonObject = json.asJsonObject
        val messageContent = when(val contentType = jsonObject.get("type").asString) {
            WhatsAppMessageContentType.TEXT.type -> gson.fromJson(jsonObject.get(contentType), WhatsAppTextMessage::class.java)
            WhatsAppMessageContentType.IMAGE.type -> gson.fromJson(jsonObject.get(contentType), WhatsAppImageMessage::class.java)
            else -> WhatsAppUnknownMessage
        }
        return SendMessageRequest(
            toPhoneNumber = jsonObject.get("to").asString,
            content = messageContent,
            messagingProduct = jsonObject.get("messaging_product").asString,
            recipientType = jsonObject.get("recipient_type").asString
        )
    }

}