package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class ChapterResponse(
    @SerializedName("id")          val id: Int,
    @SerializedName("name")        val name: String,
    @SerializedName("verse_count") val verseCount: Int
)
