package dev.romio.gptengine.model

import com.google.gson.annotations.SerializedName

data class OpenAiError(
    @SerializedName("error")
    val error: ApiError
)

data class ApiError(
    @SerializedName("code")
    val code: Any?,
    @SerializedName("message")
    val message: String,
    @SerializedName("param")
    val `param`: Any?,
    @SerializedName("type")
    val type: String
)

data class OpenAiModels(
    @SerializedName("data")
    val models: List<OpenAiModelData>,
    @SerializedName("object")
    val objectType: String
)

data class OpenAiModelData(
    @SerializedName("created")
    val created: Long?,
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("owned_by")
    val ownedBy: String?,
    @SerializedName("permission")
    val permission: List<OpenAiModelPermission>,
    @SerializedName("root")
    val root: String?
)

data class OpenAiModelPermission(
    @SerializedName("allow_create_engine")
    val allowCreateEngine: Boolean?,
    @SerializedName("allow_fine_tuning")
    val allowFineTuning: Boolean?,
    @SerializedName("allow_logprobs")
    val allowLogprobs: Boolean?,
    @SerializedName("allow_sampling")
    val allowSampling: Boolean?,
    @SerializedName("allow_search_indices")
    val allowSearchIndices: Boolean?,
    @SerializedName("allow_view")
    val allowView: Boolean?,
    @SerializedName("created")
    val created: Long?,
    @SerializedName("id")
    val id: String,
    @SerializedName("is_blocking")
    val isBlocking: Boolean?,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("organization")
    val organization: String?
)

data class OpenAiCompletionsResponse(
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("created")
    val created: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("usage")
    val usage: Usage?
)

data class OpenAiEditResponse(
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("usage")
    val usage: Usage
)

data class Usage(
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

data class Choice(
    @SerializedName("finish_reason")
    val finishReason: String?,
    @SerializedName("index")
    val index: Int,
    @SerializedName("logprobs")
    val logProbs: LogProbs?,
    @SerializedName("text")
    val text: String
)

data class LogProbs(
    @SerializedName("text_offset")
    val textOffset: List<Int>,
    @SerializedName("token_logprobs")
    val tokenLogprobs: List<Double>,
    @SerializedName("tokens")
    val tokens: List<String>,
    @SerializedName("top_logprobs")
    val topLogprobs: Any?
)

data class OpenAiImageResponse(
    @SerializedName("created")
    val created: Long,
    @SerializedName("data")
    val imageDataList: List<ImageData>
)

data class ImageData(
    @SerializedName("url")
    val url: String
)

data class OpenAiCreateModerationResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("results")
    val moderationResults: List<ModerationResult>
)

data class ModerationResult(
    @SerializedName("categories")
    val categories: Categories,
    @SerializedName("category_scores")
    val categoryScores: CategoryScores,
    @SerializedName("flagged")
    val flagged: Boolean
)

data class Categories(
    @SerializedName("hate")
    val hate: Boolean,
    @SerializedName("hate/threatening")
    val hateOrThreatening: Boolean,
    @SerializedName("self-harm")
    val selfHarm: Boolean,
    @SerializedName("sexual")
    val sexual: Boolean,
    @SerializedName("sexual/minors")
    val sexualOrMinors: Boolean,
    @SerializedName("violence")
    val violence: Boolean,
    @SerializedName("violence/graphic")
    val violenceOrGraphic: Boolean
)

data class CategoryScores(
    @SerializedName("hate")
    val hate: Double,
    @SerializedName("hate/threatening")
    val hateOrThreatening: Double,
    @SerializedName("self-harm")
    val selfHarm: Double,
    @SerializedName("sexual")
    val sexual: Double,
    @SerializedName("sexual/minors")
    val sexualOrMinors: Double,
    @SerializedName("violence")
    val violence: Double,
    @SerializedName("violence/graphic")
    val violenceOrGraphic: Double
)
