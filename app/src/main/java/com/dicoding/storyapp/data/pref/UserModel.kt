package com.dicoding.storyapp.data.pref

data class UserModel(
    val userId: String,
    val name: String,
    val token: String,
    val isLogin: Boolean = false
) {
    companion object {
        val name: String = ""
    }
}