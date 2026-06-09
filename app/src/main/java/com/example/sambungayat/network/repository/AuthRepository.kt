package com.example.sambungayat.network.repository

import com.example.sambungayat.network.ApiClient
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.model.request.LoginRequest
import com.example.sambungayat.network.model.request.RegisterRequest
import com.example.sambungayat.network.model.response.AuthResponse

class AuthRepository {

    private val api = ApiClient.apiService

    suspend fun login(email: String, password: String): ApiResult<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) ApiResult.Success(body)
                else ApiResult.Error(body?.message ?: "Login gagal")
            } else {
                ApiResult.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Tidak dapat terhubung ke server")
        }
    }

    suspend fun register(name: String, email: String, password: String): ApiResult<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) ApiResult.Success(body)
                else ApiResult.Error(body?.message ?: "Registrasi gagal")
            } else {
                ApiResult.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Tidak dapat terhubung ke server")
        }
    }
}
