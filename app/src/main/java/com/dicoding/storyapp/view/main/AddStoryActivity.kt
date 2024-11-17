package com.dicoding.storyapp.view.main

import android.Manifest
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
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.welcome.getImageUri
import com.yalantis.ucrop.UCrop
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var mainViewModel: MainViewModel

    // Permission launcher for runtime permission requests
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Camera permission granted")
            } else {
                showToast("Camera permission denied")
            }
        }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            mainViewModel.currentImageUri.value?.let {
                // Set the current image URI in the ViewModel after taking a picture
                mainViewModel.setCurrentImageUri(it)
                showImage(it)
            }
        } else {
            Log.e("Camera", "Image capture failed")
        }
    }

    private fun startCamera() {
        val imageUri = getImageUri(this)
        mainViewModel.setCurrentImageUri(imageUri)
        launcherIntentCamera.launch(imageUri)
    }

    private fun startCameraX() {
        Toast.makeText(this, "Fitur ini belum tersedia", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(applicationContext)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Observe the current image URI
        mainViewModel.currentImageUri.observe(this) { uri ->
            uri?.let {
                // Display the image in the ImageView
                showImage(it)
            }
        }

        // Check and request camera permission if not already granted
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        // Set up button click listeners
        binding.galleryButton.setOnClickListener { openGallery() }
        binding.cameraButton.setOnClickListener { requestCameraFeature() }
        binding.uploadButton.setOnClickListener { uploadStory() }
    }

    // Function to check if camera permission is granted
    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Function to open the gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    // Function to handle cropping the selected image
    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(16f, 9f)
            .withMaxResultSize(1080, 720)
            .start(this)
    }

    // Result launcher for gallery intent
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            if (selectedImg != null) {
                startCrop(selectedImg)
            } else {
                Log.e("Gallery", "No image selected")
            }
        } else {
            Log.e("Gallery", "Failed to pick image")
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(data!!)
                if (resultUri != null) {
                    mainViewModel.setCurrentImageUri(resultUri) // Update URI ke ViewModel
                    showImage(resultUri) // Tampilkan gambar yang sudah dicrop
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_CANCELED) {
            // jika cropping dibatalkan, gunakan URI gambar yang lama
            mainViewModel.currentImageUri.value?.let {
                showImage(it)
                // tampilkan gambar yang sebelumnya dipilih
            }
        }
    }

    // Function to display the selected/cropped image
    private fun showImage(uri: Uri) {
        Log.d("Image URI", "showImage: $uri")
        binding.previewImageView.setImageURI(uri)
    }

    // Placeholder function for camera feature
    private fun requestCameraFeature() {
        // Generate a URI to store the captured image
        val imageUri = getImageUri(this)

        // Save the URI in the ViewModel
        mainViewModel.setCurrentImageUri(imageUri)

        // Launch the camera to capture the image
        launcherIntentCamera.launch(imageUri)
    }

    // Function to handle story upload
    private fun uploadStory() {
        val description = binding.descriptionEditText.text.toString()
        if (description.isBlank()) {
            showToast("Description cannot be empty.")
            return
        }
        val url = mainViewModel.currentImageUri
        // Assuming the upload logic is implemented
        Log.d("Upload", "Upload button clicked. Description: $description and url: $url")
        showToast("Story uploaded successfully.")
    }

    // Utility function to show toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
