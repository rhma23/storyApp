package com.dicoding.storyapp.data

import com.dicoding.storyapp.ApiService
import response.LoginResponse
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(
            apiService: ApiService
        ): LoginRepository =
            instance ?: synchronized(this) {
                instance ?: LoginRepository(apiService)
            }.also { instance = it }
    }
}