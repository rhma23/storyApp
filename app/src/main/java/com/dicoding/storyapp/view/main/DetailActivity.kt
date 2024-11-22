package com.dicoding.storyapp.view.main

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.response.ListStoryItem
import java.io.IOException
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>("EXTRA_STORY")

        story?.let {
            Glide.with(this).load(it.photoUrl).into(binding.ivStoryImage)
            binding.tvStoryName.text = it.name
            binding.tvStoryDescription.text = it.description
            binding.tvStoryCreatedAt.text = it.createdAt

            val lat = it.lat
            val lon = it.lon

            if (lat != null && lon != null) {
                val wilayah = getAddressFromLatLng(this, lat, lon)
                binding.tvStoryLocation.text = wilayah
            } else {
                binding.tvStoryLocation.text = "Lokasi tidak tersedia"
            }
        }
    }

    private fun getAddressFromLatLng(context: Context, latitude: Any?, longitude: Any?): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude as Double, longitude as Double, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                address.locality ?: address.subAdminArea ?: address.adminArea ?: "Lokasi tidak ditemukan"
            } else {
                "Lokasi tidak ditemukan"
            }
        } catch (e: IOException) {
            Log.e("DetailActivity", "Geocoder error: ${e.message}")
            "Lokasi tidak ditemukan"
        }
    }
}
