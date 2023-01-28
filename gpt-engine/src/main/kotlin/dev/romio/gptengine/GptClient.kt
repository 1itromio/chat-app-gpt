package dev.romio.gptengine

import arrow.core.Either
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dev.romio.gptengine.model.CreateCompletionsRequest
import dev.romio.gptengine.model.CreateEditRequest
import dev.romio.gptengine.model.CreateImageRequest
import dev.romio.gptengine.model.OpenAiCompletionsResponse
import dev.romio.gptengine.model.OpenAiError
import dev.romio.gptengine.model.OpenAiImageResponse
import dev.romio.gptengine.model.OpenAiImageSize
import dev.romio.gptengine.service.OpenAiService
import dev.romio.gptengine.util.OpenAiClientError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.time.Duration
import java.util.LinkedList

class GptClient private constructor(
    private val baseUrl: String,
    private val apiKey: String,
    private val timeOut: Duration?,
    private val interceptors: LinkedList<Interceptor>
) {

    private constructor(builder: Builder): this(
        builder.baseUrl,
        builder.apiKey,
        builder.timeOut,
        builder.interceptors
    )

    companion object {
        inline fun gptClient(init: Builder.() -> Unit) = Builder().apply(init).build()
    }

    class Builder {

        var baseUrl: String = "https://api.openai.com"
        var apiKey: String = ""
        var timeOut: Duration? = null
        val interceptors: LinkedList<Interceptor> = LinkedList<Interceptor>()

        fun addInterceptor(interceptor: () -> Interceptor) {
            interceptors.add(interceptor())
        }

        fun build() = GptClient(this)
    }

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(OpenAiImageSize::class.java, object : JsonSerializer<OpenAiImageSize> {
                override fun serialize(
                    src: OpenAiImageSize?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement {
                    return JsonPrimitive(src?.value)
                }
            })
            .create()
    }

    private val openAiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val newRequest = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(newRequest)
            })
            .apply {
                interceptors.forEach {
                    addInterceptor(it)
                }
            }
            .apply {
                timeOut?.let {
                    readTimeout(it)
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        retrofit.create(OpenAiService::class.java)
    }

    suspend fun listModels() = delegateResponseHandling {
        openAiService.listModels()
    }

    suspend fun retrieveModel(modelId: String) = delegateResponseHandling {
        openAiService.retrieveModel(modelId)
    }

    suspend fun createCompletions(init: CreateCompletionsRequest.Builder.() -> Unit) =
        createCompletions(CreateCompletionsRequest.completionRequest(init))

    suspend fun createCompletions(request: CreateCompletionsRequest) = delegateResponseHandling {
        request.stream = false
        openAiService.createCompletions(request)
    }

    suspend fun createStreamingCompletions(init: CreateCompletionsRequest.Builder.() -> Unit) =
        createStreamingCompletions(CreateCompletionsRequest.completionRequest(init))

    suspend fun createStreamingCompletions(request: CreateCompletionsRequest) = delegateResponseHandling {
        request.stream = true
        openAiService.createStreamingCompletions(request)
    }.map {
        callbackFlow<OpenAiCompletionsResponse> {
            it.charStream().forEachLine {
                val resultString = it.replace("data:", "").trim()
                when {
                    resultString == "[DONE]" -> channel.close()
                    resultString.isNotEmpty() -> {
                        trySend(gson.fromJson(resultString, OpenAiCompletionsResponse::class.java))
                    }
                }
            }
            awaitClose {  }
        }.flowOn(Dispatchers.Default)
    }

    suspend fun createEdits(request: CreateEditRequest) = delegateResponseHandling {
        openAiService.createEdits(request)
    }

    suspend fun createImage(request: CreateImageRequest) = delegateResponseHandling {
        openAiService.createImage(request)
    }

    suspend fun createImageEdit(
        image: File,
        prompt: String,
        mask: File? = null,
        n: Int? = null,
        size: OpenAiImageSize? = null,
        user: String? = null
    ): Either<OpenAiClientError, OpenAiImageResponse> {

        val request = MultipartBody.Builder()
            .setType("multipart/form-data".toMediaType())
            .addFormDataPart("prompt", prompt)
            .addFormDataPart("response_format", "url")
            .addFormDataPart("image", "image", image.asRequestBody("image".toMediaTypeOrNull()))
            .apply {
                user?.let {
                    addFormDataPart("user", it)
                }
            }
            .apply {
                size?.let {
                    addFormDataPart("size", it.value)
                }
            }
            .apply {
                n?.let {
                    addFormDataPart("n", it.toString())
                }
            }
            .apply {
                mask?.let {
                    addFormDataPart("mask", "mask", it.asRequestBody("image".toMediaTypeOrNull()))
                }
            }.build()
        return delegateResponseHandling {
            openAiService.createImageEdit(request)
        }
    }

    suspend fun createImageVariation(
        image: File,
        n: Int? = null,
        size: OpenAiImageSize? = null,
        user: String? = null
    ): Either<OpenAiClientError, OpenAiImageResponse> {
        val request = MultipartBody.Builder()
            .setType("multipart/form-data".toMediaType())
            .addFormDataPart("image", "image", image.asRequestBody("image".toMediaTypeOrNull()))
            .apply {
                n?.let {
                    addFormDataPart("n", it.toString())
                }
            }
            .apply {
                size?.let {
                    addFormDataPart("size", it.value)
                }
            }
            .apply {
                user?.let {
                    addFormDataPart("user", it)
                }
            }
            .build()
        return delegateResponseHandling {
            openAiService.createImageVariation(request)
        }
    }

    private suspend fun <T> delegateResponseHandling(serviceCall: suspend () -> T): Either<OpenAiClientError, T> {
        return try {
            Either.Right(serviceCall())
        } catch (ex: Exception) {
            val error = when(ex) {
                is IOException -> OpenAiClientError.OpenAiNetworkError
                is HttpException -> kotlin.runCatching {
                    ex.response()?.errorBody()?.string()?.let {
                        val result = gson.fromJson(it, OpenAiError::class.java)
                        OpenAiClientError.OpenAiApiError(ex.response()?.code() ?: 400, result.error)
                    } ?: OpenAiClientError.UnknownOpenAiClientError(ex.message())
                }.getOrDefault(OpenAiClientError.OpenAiHttpError(ex.response()?.code() ?: 400, ex.message()))
                else -> {
                    OpenAiClientError.UnknownOpenAiClientError(ex.message ?: ex.localizedMessage ?: "")
                }
            }
            Either.Left(error)
        }
    }
}