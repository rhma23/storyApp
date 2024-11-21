package com.dicoding.storyapp.data.repository

import com.dicoding.storyapp.response.RegisterResponse
import com.dicoding.storyapp.config.ApiService
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    companion object {
        @Volatile
        private var instance: RegisterRepository? = null
        fun getInstance(
            apiService: ApiService
        ): RegisterRepository =
            instance ?: synchronized(this) {
                instance ?: RegisterRepository(apiService)
            }.also { instance = it }
    }
}