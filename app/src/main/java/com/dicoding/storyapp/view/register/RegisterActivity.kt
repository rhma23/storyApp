package com.dicoding.storyapp.view.register

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.config.RetrofitClient
import com.dicoding.storyapp.factory.RegisterViewModelFactory
import com.dicoding.storyapp.data.repository.RegisterRepository
import com.dicoding.storyapp.databinding.ActivitySignupBinding
import com.dicoding.storyapp.ui.EmailValidation
import com.dicoding.storyapp.ui.PasswordValidation

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var repository: RegisterRepository
    private lateinit var passwordValidation: PasswordValidation
    private lateinit var emailValidation: EmailValidation

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = RegisterRepository.getInstance(RetrofitClient.apiService)

        setupView()
        setupAction()
        passwordValidation = binding.passwordEditText
        emailValidation = binding.emailEditText
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
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val TAG = "setupAction"
            Log.d(TAG, "setupAction: $name, $email, $password")

            if (binding.emailEditTextLayout.error == null && binding.passwordEditTextLayout.error == null) {
                registerViewModel.registerUser(name, email, password) { success ->
                    if (success) {
                        AlertDialog.Builder(this).apply {
                            setTitle("Yeah!")
                            setMessage("Akun dengan $email sudah jadi nih. Yuk, mulai buat dan lihat story seru dari teman-teman!")
                            setPositiveButton("Lanjut") { _, _ -> finish() }
                            create()
                            show()
                        }
                    } else {
                        AlertDialog.Builder(this).apply {
                            setTitle("Oops!")
                            setMessage("Akun dengan $email gagal didaftarkan. Coba lagi ya!")
                            setPositiveButton("Ulangi") { _, _ -> finish() }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }
}
