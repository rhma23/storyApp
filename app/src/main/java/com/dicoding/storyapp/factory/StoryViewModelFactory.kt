package com.dicoding.storyapp.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.injection.StoryInjection
import com.dicoding.storyapp.view.main.AddStoryViewModel

class StoryViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): StoryViewModelFactory {
            if (INSTANCE == null) {
                synchronized(StoryViewModelFactory::class.java) {
                    INSTANCE = StoryViewModelFactory(StoryInjection.provideStoryRepository(context))
                }
            }
            return INSTANCE as StoryViewModelFactory
        }
    }
}