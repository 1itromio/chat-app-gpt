package dev.romio.gptengine.util

import dev.romio.gptengine.model.ApiError

sealed class Error(val msg: String) {
    open class HttpError(val responseCode: Int, msg: String): Error(msg)
    class OpenAiError(responseCode: Int, val error: ApiError): HttpError(responseCode, error.message)
    object NetworkError: Error("Failed to establish network connection")
    class UnknownError(msg: String): Error(msg)
}