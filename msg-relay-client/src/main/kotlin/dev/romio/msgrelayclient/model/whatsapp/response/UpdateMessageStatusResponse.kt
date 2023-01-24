package dev.romio.msgrelayclient.model.whatsapp.response


import com.google.gson.annotations.SerializedName

data class UpdateMessageStatusResponse(
    @SerializedName("success")
    val success: Boolean
)