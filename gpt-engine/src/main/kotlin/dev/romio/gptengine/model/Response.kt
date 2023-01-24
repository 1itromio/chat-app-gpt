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
    val usage: Usage
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
    val logprobs: Any?,
    @SerializedName("text")
    val text: String
)

