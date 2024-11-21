package com.dicoding.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.adapter.StoryAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        storyAdapter = StoryAdapter(emptyList()) { story ->
            // When an item is clicked, start DetailActivity with the selected story
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_STORY", story) // Pass the selected story as a Parcelable extra
            }
            startActivity(intent)
        }

        binding.rvUpcoming.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        // Observe session and check login status
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        // Observe stories data
        viewModel.storyLiveData.observe(this) { stories ->
            storyAdapter = stories?.let {
                StoryAdapter(it) { story ->
                    // Handle item click here as well, if you want additional actions
                    val intent = Intent(this, DetailActivity::class.java).apply {
                        putExtra("EXTRA_STORY", story) // Pass the selected story as a Parcelable extra
                    }
                    startActivity(intent)
                }
            }!!
            binding.rvUpcoming.adapter = storyAdapter
        }


        // Fetch stories from API
        viewModel.getAllStories(false)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }
}
