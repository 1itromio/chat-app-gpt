package dev.romio.msgrelayclient.error

sealed class RelayError(val msg: String) {
    class HttpError(val httpStatusCode: Int, msg: String): RelayError(msg)
    class ClientError(val httpStatusCode: Int, relayClientError: RelayClientError): RelayError(relayClientError.msg)
    class UnknownError(msg: String): RelayError(msg)
    object NetworkError: RelayError("Network Error")
}