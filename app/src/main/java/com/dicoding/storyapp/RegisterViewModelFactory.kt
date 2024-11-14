package com.dicoding.storyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.RegisterRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.viewmodel.RegisterViewModel

class RegisterViewModelFactory(
    private val repository: RegisterRepository // Ganti dengan parameter yang Anda butuhkan
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            RegisterViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
