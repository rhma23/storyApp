// RegisterViewModel.kt
package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import response.LoginResponse

private lateinit var userPreference: UserPreference


class LoginViewModel (private val repository: UserRepository) : ViewModel() {

    // Fungsi untuk registrasi pengguna
    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit,
        dataStore: DataStore<Preferences>
    ) {
        userPreference = UserPreference.getInstance(dataStore)
        val TAG = "loginUser"
        viewModelScope.launch {
            try {
                val response: LoginResponse = repository.login(email, password)
                Log.d(TAG, "loginUser: ${response.loginResult?.token}")
                if (response.error == false) {
                    CoroutineScope(Dispatchers.IO).launch {
                        response.loginResult?.userId?.let { response.loginResult.name?.let { it1 ->
                            response.loginResult.token?.let { it2 ->
                                UserModel(it,
                                    it1, it2
                                )
                            }
                        } }
                            ?.let { userPreference.saveSession(it) }
                    }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "loginUser: ", e)
                onResult(false)
            }
        }
    }
}
