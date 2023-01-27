package dev.romio.gptwebhookservice.model.response.config

import com.google.gson.annotations.SerializedName

data class ConfigResponse(
    @SerializedName("maxConversationSize")
    val maxConversationSize: Int,
    @SerializedName("whatsAppPhoneNumId")
    val whatsAppPhoneNumId: String,
    @SerializedName("whatsAppVerifyToken")
    val whatsAppVerifyToken: String,
    @SerializedName("whatsAppApiKey")
    val whatsAppApiKey: String,
    @SerializedName("openAiApiKey")
    val openAiApiKey: String,
    @SerializedName("startingPrompt")
    val startingPrompt: String,
    @SerializedName("textModel")
    val textModel: String,
    @SerializedName("codeModel")
    val codeModel: String,
    @SerializedName("tgBotToken")
    val tgBotToken: String,
    @SerializedName("tgBotMode")
    val tgBotMode: String,
    @SerializedName("domain")
    val domain: String
)