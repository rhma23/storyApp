package com.dicoding.storyapp.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class PasswordValidation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {


    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                validatePassword(s.toString())
            }
        })
    }


    private fun validatePassword(password: String) {
        val minLength = 8
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        when {
            password.length < minLength -> {
                error = "Password harus minimal $minLength karakter"
            }
            !hasDigit -> {
                error = "Password harus mengandung angka"
            }
            !hasSpecialChar -> {
                error = "Password harus mengandung setidaknya special character"
            }
            else -> {
                error = null
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        return false
    }

}