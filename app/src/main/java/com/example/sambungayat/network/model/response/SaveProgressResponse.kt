package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class SaveProgressResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
