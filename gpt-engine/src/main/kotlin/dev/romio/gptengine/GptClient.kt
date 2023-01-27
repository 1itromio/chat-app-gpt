package dev.romio.gptengine

import arrow.core.Either
import com.google.gson.GsonBuilder
import dev.romio.gptengine.model.CreateCompletionsRequest
import dev.romio.gptengine.model.CreateEditRequest
import dev.romio.gptengine.model.OpenAiError
import dev.romio.gptengine.service.OpenAiService
import dev.romio.gptengine.util.Error
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class GptClient constructor(apiKey: String) {

    companion object {
        private const val HEADER_AUTH = "Authorization"
        private const val BASE_URL = "https://api.openai.com"
    }

    private val gson by lazy {
        GsonBuilder().create()
    }

    private val openAiService by lazy {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val newRequest = chain.request()
                    .newBuilder()
                    .addHeader(HEADER_AUTH, "Bearer $apiKey")
                    .build()
                chain.proceed(newRequest)
            })
            .addInterceptor(logger)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        retrofit.create(OpenAiService::class.java)
    }

    suspend fun listModels() = delegateResponseHandling {
        openAiService.listModels()
    }

    suspend fun getModel(modelId: String) = delegateResponseHandling {
        openAiService.getModel(modelId)
    }

    suspend fun createCompletions(request: CreateCompletionsRequest) = delegateResponseHandling {
        openAiService.createCompletions(request)
    }

    suspend fun createEdits(request: CreateEditRequest) = delegateResponseHandling {
        openAiService.createEdits(request)
    }

    private suspend fun <T> delegateResponseHandling(serviceCall: suspend () -> T): Either<Error, T> {
        return try {
            Either.Right(serviceCall())
        } catch (ex: Exception) {
            val error = when(ex) {
                is IOException -> Error.NetworkError
                is HttpException -> kotlin.runCatching {
                    ex.response()?.errorBody()?.string()?.let {
                        val result = gson.fromJson(it, OpenAiError::class.java)
                        Error.OpenAiError(ex.response()?.code() ?: 400, result.error)
                    } ?: Error.UnknownError(ex.message())
                }.getOrDefault(Error.HttpError(ex.response()?.code() ?: 400, ex.message()))
                else -> {
                    Error.UnknownError(ex.message ?: ex.localizedMessage ?: "")
                }
            }
            Either.Left(error)
        }
    }
}