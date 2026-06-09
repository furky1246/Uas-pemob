package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class VerseResponse(
    @SerializedName("verse_number") val verseNumber: Int,
    @SerializedName("text")         val text: String?
)
