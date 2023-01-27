package dev.romio.gptengine.model

import com.google.gson.annotations.SerializedName

data class CreateCompletionsRequest(
    @SerializedName("model")
    val model: String,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("temperature")
    val temperature: Float = 0.9f,
    @SerializedName("top_p")
    val topP: Float = 1.0f,
    @SerializedName("stop")
    val stop: List<String>? = null,
    @SerializedName("n")
    val n: Int = 1,
    @SerializedName("max_tokens")
    val maxTokens: Int = 150,
    @SerializedName("stream")
    val stream: Boolean = false,
    @SerializedName("echo")
    val echo: Boolean = false,
    @SerializedName("presence_penalty")
    val presencePenalty: Float = 0f,
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Float = 0f,
    @SerializedName("best_of")
    val bestOf: Int = 1,
    @SerializedName("logit_bias")
    val logitBias: Map<String, Int>? = null,
    @SerializedName("logprobs")
    val logProbs: Int? = null,
    @SerializedName("suffix")
    val suffix: String? = null,
    @SerializedName("user")
    val user: String? = null
)

data class CreateEditRequest(
    @SerializedName("input")
    val input: String,
    @SerializedName("instruction")
    val instruction: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("temperature")
    val temperature: Float = 0.6f,
    @SerializedName("top_p")
    val topP: Float = 1.0f,
    @SerializedName("n")
    val n: Int = 1,
)

enum class OpenAiImageSize(val value: String) {
    SMALL("256x256"), MEDIUM("512x512"), LARGE("1024x1024")
}

data class CreateImageRequest(
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("n")
    val n: Int = 1,
    @SerializedName("size")
    val size: OpenAiImageSize? = null,
    @SerializedName("response_format")
    val responseFormat: String = "url",
    @SerializedName("user")
    val user: String? = null
)

data class CreateModerationRequest(
    @SerializedName("input")
    val input: String
)