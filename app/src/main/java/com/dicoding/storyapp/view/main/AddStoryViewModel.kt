package com.dicoding.storyapp.view.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.response.AddNewStoryResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _uploadResponse = MutableLiveData<AddNewStoryResponse>()
    val uploadResponse: LiveData<AddNewStoryResponse> = _uploadResponse

    fun uploadStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        latPart: RequestBody?,
        lonPart: RequestBody?
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.uploadNewStory(token, photo, description, latPart, lonPart)
                }
                Log.d(TAG, "Response: $response")
                _uploadResponse.postValue(response)
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error: ${e.message()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }
}