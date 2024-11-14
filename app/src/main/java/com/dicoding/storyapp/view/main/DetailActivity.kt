package com.dicoding.storyapp.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.dicoding.storyapp.response.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data story yang diteruskan dari MainActivity
        val story = intent.getParcelableExtra<ListStoryItem>("EXTRA_STORY")

        // Menampilkan data ke dalam UI
        story?.let {
            Glide.with(this).load(it.photoUrl).into(binding.ivStoryImage)
            binding.tvStoryName.text = it.name
            binding.tvStoryDescription.text = it.description
            binding.tvStoryCreatedAt.text = it.createdAt
            // Misalnya, untuk menampilkan latitude dan longitude
            binding.tvStoryLocation.text = "Lat: ${it.lat}, Lon: ${it.lon}"
        }
    }
}
