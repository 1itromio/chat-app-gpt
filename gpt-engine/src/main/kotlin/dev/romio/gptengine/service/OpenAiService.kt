package dev.romio.gptengine.service

import dev.romio.gptengine.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OpenAiService {
    @GET("/v1/models")
    suspend fun listModels(): OpenAiModels

    @GET("/v1/models/{model}")
    suspend fun retrieveModel(@Path("model") model: String): OpenAiModelData

    // Text Related Apis
    @POST("/v1/completions")
    suspend fun createCompletions(@Body request: CreateCompletionsRequest): OpenAiCompletionsResponse

    @POST("/v1/edits")
    suspend fun createEdits(@Body request: CreateEditRequest): OpenAiEditResponse

    // Image Related Apis
    @POST("/v1/images/generations")
    suspend fun createImage(@Body request: CreateImageRequest): OpenAiImageResponse

    @POST("/v1/images/edits")
    suspend fun createImageEdit(@Body request: RequestBody): OpenAiImageResponse

    @POST("/v1/images/variations")
    suspend fun createImageVariation(@Body request: RequestBody): OpenAiImageResponse

    // Moderation related Apis
    @POST("/v1/moderations")
    suspend fun createModeration(@Body request: CreateModerationRequest): OpenAiCreateModerationResponse

}