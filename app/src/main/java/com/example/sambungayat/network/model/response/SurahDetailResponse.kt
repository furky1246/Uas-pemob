package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class SurahDetailResponse(
    @SerializedName("surah")  val surah: SurahInfo,
    @SerializedName("verses") val verses: List<VerseDetail>
)

data class SurahInfo(
    @SerializedName("id")          val id: Int,
    @SerializedName("name")        val name: String,
    @SerializedName("verse_count") val verseCount: Int
)

data class VerseDetail(
    @SerializedName("verse_number") val verseNumber: Int,
    @SerializedName("arabic")       val arabic: String?,
    @SerializedName("tafsir")       val tafsir: String?,
    @SerializedName("audio_url")    val audioUrl: String?
)
