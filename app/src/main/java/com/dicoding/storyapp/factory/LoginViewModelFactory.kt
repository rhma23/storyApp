package com.dicoding.storyapp.factory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.viewmodel.LoginViewModel

class LoginViewModelFactory(
    private val repository: UserRepository // Ganti dengan parameter yang Anda butuhkan
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
