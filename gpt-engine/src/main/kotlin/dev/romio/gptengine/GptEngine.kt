package dev.romio.gptengine

import arrow.core.Either
import com.google.gson.GsonBuilder
import dev.romio.gptengine.interceptor.OkHttpHeaderInterceptor
import dev.romio.gptengine.model.CreateCompletionsRequest
import dev.romio.gptengine.model.CreateEditRequest
import dev.romio.gptengine.model.OpenAiError
import dev.romio.gptengine.service.OpenAiService
import dev.romio.gptengine.util.Error
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object GptEngine {

    private const val BASE_URL = "https://api.openai.com"

    private val mutexLock = Mutex()

    private lateinit var apiKey: String
    private lateinit var openAiService: OpenAiService

    var isInitialised: Boolean = false
        private set

    private val gson by lazy {
        GsonBuilder().create()
    }

    suspend fun init(apiKey: String) = mutexLock.withLock {
        if(isInitialised) {
            return@withLock
        }
        this.apiKey = apiKey
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(OkHttpHeaderInterceptor(this.apiKey))
            .build()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        openAiService = retrofit.create(OpenAiService::class.java)
        isInitialised = true
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