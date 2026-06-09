package com.example.sambungayat.network.repository

import com.example.sambungayat.network.ApiClient
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.model.request.SaveProgressRequest
import com.example.sambungayat.network.model.response.ProfileResponse
import com.example.sambungayat.network.model.response.ProgressResponse
import com.example.sambungayat.network.model.response.SaveProgressResponse
import com.example.sambungayat.network.model.response.StatisticsResponse

class UserRepository {

    private val api = ApiClient.apiService

    suspend fun getProfile(userId: Int): ApiResult<ProfileResponse> {
        return try {
            val response = api.getProfile(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Data profil kosong")
            } else {
                ApiResult.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Tidak dapat terhubung ke server")
        }
    }

    suspend fun getProgress(userId: Int): ApiResult<ProgressResponse> {
        return try {
            val response = api.getProgress(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Data progress kosong")
            } else {
                ApiResult.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Tidak dapat terhubung ke server")
        }
    }

    suspend fun getStatistics(userId: Int): ApiResult<StatisticsResponse> {
        return try {
            val response = api.getStatistics(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Data statistik kosong")
            } else {
                ApiResult.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Tidak dapat terhubung ke server")
        }
    }

    suspend fun saveProgress(
        userId: Int,
        totalScore: Int,
        bestStreak: Int,
        highestUnlockedSurah: Int,
        currentSurah: Int,
        currentVerse: Int
    ): ApiResult<SaveProgressResponse> {
        return try {
            val request = SaveProgressRequest(
                userId, totalScore, bestStreak,
                highestUnlockedSurah, currentSurah, currentVerse
            )
            val response = api.saveProgress(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) ApiResult.Success(body)
                else ApiResult.Error(body?.message ?: "Gagal menyimpan progress")
            } else {
                ApiResult.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Tidak dapat terhubung ke server")
        }
    }
}
