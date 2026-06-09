package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id")    val id: Int,
    @SerializedName("name")  val name: String,
    @SerializedName("email") val email: String
)
