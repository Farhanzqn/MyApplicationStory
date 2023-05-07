package com.zidan.myapplicationstory.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.zidan.myapplicationstory.R
import com.zidan.myapplicationstory.databinding.ActivityLoginBinding
import com.zidan.myapplicationstory.main.MainActivity
import com.zidan.myapplicationstory.main.UserViewModel
import com.zidan.myapplicationstory.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        login()
        setupAction()
        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        userViewModel.userStatus.observe(this) {
            if (!it) {
                Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun login() {
        userViewModel.getDataSession().observe(this) {
            if (it.token.trim() != "") {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }
    private fun setupAction() {
        binding.apply {
            btnSignIn.setOnClickListener {
                userLogin()
            }
            tvSignUp.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }

        binding.apply {
            btnSignIn.isEnabled = !isLoading
            tvSignUp.isEnabled = !isLoading
            emailAccount.isEnabled = !isLoading
            passwordAccount.isEnabled = !isLoading
        }
    }
    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun userLogin() {
        val email = binding.emailAccount.text.toString()
        val password = binding.passwordAccount.text.toString()

        when {
            email.isEmpty() -> {
                binding.emailAccount.error = getString(R.string.email_invalid)
            }
            password.isEmpty() -> {
                binding.passwordAccount.error = getString(R.string.password_min)
            }
            else -> {
                if (checkEmail(email) && password.length >= 6) {
                    userViewModel.userLogin(LoginUser(email, password))
                } else {
                    Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
                }

                userViewModel.loginResult.observe(this) {
                    userViewModel.saveDataSession(it)
                }
            }
        }

    }
}