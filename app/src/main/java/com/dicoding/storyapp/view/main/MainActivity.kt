package com.dicoding.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.StoryAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyAdapter = StoryAdapter(emptyList()) { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_STORY", story)
            }
            startActivity(intent)
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        viewModel.storyLiveData.observe(this) { stories ->
            storyAdapter = stories?.let {
                StoryAdapter(it) { story ->
                    val intent = Intent(this, DetailActivity::class.java).apply {
                        putExtra("EXTRA_STORY", story)
                    }
                    startActivity(intent)
                }
            }!!
            binding.rvStory.adapter = storyAdapter
        }

        viewModel.getAllStories(false)

        binding.progressBarHome.visibility = View.VISIBLE
        binding.rvStory.visibility = View.INVISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.storyLiveData.observe(this) { stories ->
                if (stories.isNullOrEmpty()) {
                    binding.rvStory.visibility = View.INVISIBLE
                } else {
                    binding.rvStory.visibility = View.VISIBLE
                }
                binding.progressBarHome.visibility = View.GONE
            }
        }, 1000)
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

        val toolbar = binding.toolbar
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }
}