package com.example.sambungayat.network.model.response

import com.google.gson.annotations.SerializedName

data class ProgressResponse(
    @SerializedName("total_score")            val totalScore: Int,
    @SerializedName("best_streak")            val bestStreak: Int,
    @SerializedName("highest_unlocked_surah") val highestUnlockedSurah: Int,
    @SerializedName("current_surah")          val currentSurah: Int,
    @SerializedName("current_verse")          val currentVerse: Int
)
