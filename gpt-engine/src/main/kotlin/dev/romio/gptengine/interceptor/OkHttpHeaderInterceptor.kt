package dev.romio.gptengine.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class OkHttpHeaderInterceptor(private val openApiKey: String): Interceptor {

    companion object {
        private const val HEADER_AUTH = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request()
        val headerBuilder = newRequest.headers.newBuilder()
        headerBuilder.add(HEADER_AUTH, "Bearer $openApiKey")
        return chain.proceed(newRequest)
    }
}