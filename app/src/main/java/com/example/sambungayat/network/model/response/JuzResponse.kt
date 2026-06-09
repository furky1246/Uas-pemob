package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class JuzResponse(
    @SerializedName("id")     val id: Int,
    @SerializedName("name")   val name: String,
    @SerializedName("number") val number: Int
)
