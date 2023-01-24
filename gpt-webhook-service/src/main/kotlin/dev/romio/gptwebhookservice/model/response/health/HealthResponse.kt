package dev.romio.gptwebhookservice.model.response.health

import com.google.gson.annotations.SerializedName

data class HealthResponse(
    @SerializedName("status")
    val status: String
)