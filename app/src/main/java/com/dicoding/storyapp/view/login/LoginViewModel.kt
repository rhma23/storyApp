// RegisterViewModel.kt
package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.LoginRepository
import com.dicoding.storyapp.data.UserRepository
import kotlinx.coroutines.launch
import response.LoginResponse


class LoginViewModel (private val repository: UserRepository) : ViewModel() {

    // Fungsi untuk registrasi pengguna
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        val TAG = "loginUser"
        Log.d(TAG, "loginUser: $email, $password")
        viewModelScope.launch {
            try {
                val response: LoginResponse = repository.login(email, password)
                if (response.error == false) {
                    val TAG = "LoginUser";
                    Log.d(TAG, "loginUser: ${response.message}")
                    onResult(true)
                } else {
                    val TAG = "loginUser";
                    Log.d(TAG, "loginUser: else")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "loginUser: ", e)
                onResult(false)
            }
        }
    }
}
