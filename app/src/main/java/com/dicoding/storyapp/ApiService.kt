package com.dicoding.storyapp

import response.LoginResponse
import response.RegisterResponse
import response.StoryResponse

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @Headers("Content-Type: multipart/form-data")
    @GET("getAllStories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): StoryResponse
}
