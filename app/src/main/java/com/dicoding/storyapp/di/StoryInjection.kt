package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.ApiService
import com.dicoding.storyapp.data.StoryRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StoryInjection {
    private fun provideApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = provideApiService()
        return StoryRepository(apiService)
    }
}