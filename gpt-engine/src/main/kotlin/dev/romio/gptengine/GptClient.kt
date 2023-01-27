package dev.romio.gptengine

import arrow.core.Either
import com.google.gson.*
import dev.romio.gptengine.model.*
import dev.romio.gptengine.service.OpenAiService
import dev.romio.gptengine.util.OpenAiClientError
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

class GptClient constructor(apiKey: String) {

    companion object {
        private const val HEADER_AUTH = "Authorization"
        private const val BASE_URL = "https://api.openai.com"
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

    suspend fun retrieveModel(modelId: String) = delegateResponseHandling {
        openAiService.retrieveModel(modelId)
    }

    suspend fun createCompletions(request: CreateCompletionsRequest) = delegateResponseHandling {
        openAiService.createCompletions(request)
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