package com.dicoding.storyapp.data.repository

import com.dicoding.storyapp.config.ApiService
import com.dicoding.storyapp.response.AddNewStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {

    suspend fun uploadNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): AddNewStoryResponse {
        return apiService.uploadNewStory("Bearer $token", photo, description, lat, lon)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}