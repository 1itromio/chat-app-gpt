package dev.romio.gptengine.util

import dev.romio.gptengine.model.ApiError

sealed class OpenAiClientError(val msg: String) {
    open class OpenAiHttpError(val responseCode: Int, msg: String): OpenAiClientError(msg)
    class OpenAiApiError(responseCode: Int, val error: ApiError): OpenAiHttpError(responseCode, error.message)
    object OpenAiNetworkError: OpenAiClientError("Failed to establish network connection")
    class UnknownOpenAiClientError(msg: String): OpenAiClientError(msg)
}