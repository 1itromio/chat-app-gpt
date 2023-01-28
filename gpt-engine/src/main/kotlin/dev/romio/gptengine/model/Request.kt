package dev.romio.gptengine.model

import com.google.gson.annotations.SerializedName

class CreateCompletionsRequest private constructor(
    @SerializedName("model")
    val model: String,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("top_p")
    val topP: Float,
    @SerializedName("stop")
    val stop: List<String>?,
    @SerializedName("n")
    val n: Int,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    @SerializedName("echo")
    val echo: Boolean,
    @SerializedName("presence_penalty")
    val presencePenalty: Float,
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Float,
    @SerializedName("best_of")
    val bestOf: Int,
    @SerializedName("logit_bias")
    val logItBias: Map<String, Int>?,
    @SerializedName("logprobs")
    val logProbs: Int?,
    @SerializedName("suffix")
    val suffix: String?,
    @SerializedName("user")
    val user: String?
) {
    @SerializedName("stream")
    var stream: Boolean = false
        internal set

    private constructor(builder: Builder): this(
        builder.model,
        builder.prompt,
        builder.temperature,
        builder.topP,
        builder.stop,
        builder.n,
        builder.maxTokens,
        builder.echo,
        builder.presencePenalty,
        builder.frequencyPenalty,
        builder.bestOf,
        builder.logItBias,
        builder.logProbs,
        builder.suffix,
        builder.user
    )

    companion object {
        inline fun completionRequest(init: Builder.() -> Unit) = Builder().apply(init).build()
    }

    class Builder {
        var model = ""
        var prompt = ""
        var temperature: Float = 0.9f
        var topP: Float = 1.0f
        var stop: List<String>? = null
        var n: Int = 1
        var maxTokens: Int = 150
        var echo: Boolean = false
        var presencePenalty: Float = 0f
        var frequencyPenalty: Float = 0f
        var bestOf: Int = 1
        var logItBias: Map<String, Int>? = null
        var logProbs: Int? = null
        var suffix: String? = null
        var user: String? = null

        fun build() = CreateCompletionsRequest(this)
    }
}

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