package dev.romio.gptwebhookservice.model

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dev.romio.gptwebhookservice.model.request.whatsapp.Message
import dev.romio.gptwebhookservice.model.request.whatsapp.Text
import dev.romio.gptwebhookservice.model.request.whatsapp.UnknownMessage
import java.lang.reflect.Type

class WhatsAppMessageSerializationAdapter : JsonSerializer<Message>, JsonDeserializer<Message> {

    private val gson by lazy {
        GsonBuilder().create()
    }

    override fun serialize(src: Message?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null) {
            throw IllegalArgumentException("Provided source for WhatsAppMessageSerialization is null")
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", src.type)
        jsonObject.addProperty("from", src.from)
        jsonObject.addProperty("id", src.id)
        jsonObject.addProperty("timestamp", src.timestamp)
        jsonObject.addProperty(src.type, gson.toJson(src.content))
        return jsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Message {
        if (json == null) {
            throw JsonParseException("Invalid or null WhatsApp Message object passed for deserialization")
        }
        val jsonObject = json.asJsonObject
        val msgType = jsonObject.get("type").asString
        val whatsAppMessageObject = jsonObject.get(msgType)
        val whatsAppMessage = when (msgType) {
            "text" -> gson.fromJson(whatsAppMessageObject, Text::class.java)
            else -> UnknownMessage
        }
        return Message(
            from = jsonObject.get("from").asString,
            id = jsonObject.get("id").asString,
            timestamp = jsonObject.get("timestamp").asLong,
            content = whatsAppMessage,
            type = msgType
        )
    }
}
