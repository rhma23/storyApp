package com.dicoding.storyapp.view.main

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.factory.StoryViewModelFactory
import com.dicoding.storyapp.view.welcome.getImageUri
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var token: String? = null

    // Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showToast("Camera permission denied")
            }
        }



    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            mainViewModel.currentImageUri.value?.let { uri ->
                mainViewModel.setCurrentImageUri(uri)
                showImage(uri)
            }
        } else {
            Log.e("Camera", "Image capture failed")
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            if (selectedImg != null) {
                startCrop(selectedImg)
            } else {
                showToast("No image selected.")
            }
        } else {
            Log.e("Gallery", "Failed to pick image")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(applicationContext)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val addStoryFactory = StoryViewModelFactory.getInstance(applicationContext)
        addStoryViewModel = ViewModelProvider(this, addStoryFactory)[AddStoryViewModel::class.java]

        mainViewModel.currentImageUri.observe(this) { uri ->
            uri?.let {
                showImage(it)
            }
        }

        mainViewModel.getSession().observe(this) { userModel ->
            token = userModel.token
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.apply {
            galleryButton.setOnClickListener { openGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadStory() }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val imageUri = getImageUri(this)
        mainViewModel.setCurrentImageUri(imageUri)
        launcherIntentCamera.launch(imageUri)
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(16f, 9f)
            .withMaxResultSize(1080, 720)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                mainViewModel.setCurrentImageUri(resultUri)
                showImage(resultUri)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun showImage(uri: Uri) {
        Log.d("Image URI", "showImage: $uri")
        binding.previewImageView.setImageURI(uri)
    }

    private fun uploadStory() {
        val description = binding.descriptionEditText.text.toString().trim()
        if (description.isBlank()) {
            showToast("Description cannot be empty.")
            return
        }

        val imageUri = mainViewModel.currentImageUri.value
        if (imageUri == null) {
            showToast("No image selected.")
            return
        }

        val imageFile: File = if (!File(imageUri.path!!).exists()) {
            val contentUri = Uri.parse(imageUri.toString())
            val destinationFile = File(cacheDir, "output_image.jpg")
            copyFileFromURI(this, contentUri, destinationFile)
        } else {
            File(imageUri.path!!)
        }

        // Compress the image if it's larger than 1MB
        val compressedFile = if (imageFile.length() > 1_048_576) {
            compressImage(this, Uri.fromFile(imageFile))
        } else {
            imageFile
        }

        val imagePart = MultipartBody.Part.createFormData(
            "photo", compressedFile.name, compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

        // Optional latitude and longitude
        val latPart: RequestBody? = null // Replace with actual latitude if available
        val lonPart: RequestBody? = null // Replace with actual longitude if available

        token?.let {
            Log.d(TAG, "uploadStory: Token: $it")
            addStoryViewModel.uploadStory(it, imagePart, descriptionPart, latPart, lonPart)

            addStoryViewModel.uploadResponse.observe(this) { response ->
                if (!response.error!!) {
                    showToast("Story uploaded successfully!")
                } else {
                    showToast("Upload failed: ${response.message}")
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } ?: run {
            showToast("Token is not available.")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    fun copyFileFromURI(context: Context, contentUri: Uri, destinationFile: File): File {
        context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return destinationFile // Return the destination file
    }

    fun compressImage(context: Context, imageUri: Uri, maxSizeInBytes: Int = 1_048_576): File {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        var compressedBitmap = originalBitmap
        var quality = 100
        val outputStream = ByteArrayOutputStream()

        do {
            outputStream.reset()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5 // Reduce quality by 5% in each iteration
        } while (outputStream.size() > maxSizeInBytes && quality > 0)

        val compressedFile = File(context.cacheDir, "compressed_image.jpg")
        FileOutputStream(compressedFile).use { fos ->
            fos.write(outputStream.toByteArray())
        }

        return compressedFile
    }

}