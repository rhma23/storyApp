package com.dicoding.storyapp.view.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.config.RetrofitClient
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.response.AddNewStoryResponse
import com.dicoding.storyapp.response.ListStoryItem
import com.dicoding.storyapp.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyLiveData = MutableLiveData<List<ListStoryItem?>?>()
    val storyLiveData: MutableLiveData<List<ListStoryItem?>?> get() = _storyLiveData

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _uploadResponse = MutableLiveData<AddNewStoryResponse>()

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }


    fun getAllStories(withLocation: Boolean) {
        viewModelScope.launch {

            repository.getSession().collect { user ->
                val token = user.token
                Log.d("Token", "Token: $token")

                val locationParam = if (withLocation) "1" else "0"

                RetrofitClient.apiService.getAllStories("Bearer $token", locationParam).enqueue(object : Callback<StoryResponse> {
                    override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { storyResponse ->
                                _storyLiveData.value = storyResponse.listStory
                                Log.d("StoryViewModel", "Data fetched: ${storyResponse.listStory}")
                            } ?: run {
                                Log.e("StoryViewModel", "Response body is null")
                            }
                        } else {
                            Log.e("StoryViewModel", "API response not successful: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        Log.e("StoryViewModel", "API call failed: ${t.message}")
                    }
                })
            }
        }
    }
}
