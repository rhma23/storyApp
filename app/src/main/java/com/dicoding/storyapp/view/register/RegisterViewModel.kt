// RegisterViewModel.kt
package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.RegisterRepository
import kotlinx.coroutines.launch
import com.dicoding.storyapp.response.RegisterResponse


class RegisterViewModel (private val repository: RegisterRepository) : ViewModel() {

    // Fungsi untuk registrasi pengguna
    fun registerUser(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        val TAG = "registerUser"
        Log.d(TAG, "registerUser: $name, $email, $password")
        viewModelScope.launch {
            try {
                val response: RegisterResponse = repository.register(name, email, password)
                if (response.error == false) {
                    val TAG = "registerUser";
                    Log.d(TAG, "registerUser: ${response.message}")
                    onResult(true)
                } else {
                    val TAG = "registerUser";
                    Log.d(TAG, "registerUser: else")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "registerUser: ", e)
                onResult(false)
            }
        }
    }
}
