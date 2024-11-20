package com.dicoding.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.di.StoryInjection
import com.dicoding.storyapp.view.main.AddStoryViewModel

class ViewModelFactoryStory(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
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
        private var INSTANCE: ViewModelFactoryStory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactoryStory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactoryStory::class.java) {
                    INSTANCE = ViewModelFactoryStory(StoryInjection.provideStoryRepository(context))
                }
            }
            return INSTANCE as ViewModelFactoryStory
        }
    }
}