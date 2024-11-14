// RegisterViewModel.kt
package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.ApiService
import com.dicoding.storyapp.RetrofitClient.apiService
import com.dicoding.storyapp.data.RegisterRepository
import com.dicoding.storyapp.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import response.RegisterResponse
import javax.inject.Inject
import kotlin.math.log


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
