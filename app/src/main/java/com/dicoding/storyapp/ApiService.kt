package com.dicoding.storyapp

import com.dicoding.storyapp.response.AddNewStoryResponse
import com.dicoding.storyapp.response.LoginResponse
import com.dicoding.storyapp.response.RegisterResponse
import com.dicoding.storyapp.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("location") location: String // "0" for stories without location, "1" for stories with location
    ): Call<StoryResponse>


    @Multipart
    @POST("stories")
    suspend fun uploadNewStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): AddNewStoryResponse
}
