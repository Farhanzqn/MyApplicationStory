package com.zidan.myapplicationstory.register

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zidan.myapplicationstory.R
import com.zidan.myapplicationstory.databinding.ActivityRegisterBinding
import com.zidan.myapplicationstory.main.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    companion object {
        private val DELLAY_MILLIS = 3000L
    }

    private lateinit var binding: ActivityRegisterBinding

    private val userViewModel by viewModels<UserViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.signUpResult.observe(this) {
            Log.d(ContentValues.TAG, "onCreate : $it")
            if (it.error != false) {
                showSnackbar(getString(R.string.register))
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, DELLAY_MILLIS)
            }
        }

        userViewModel.snackbar.observe(this) {
            Snackbar.make(
                binding.btnRegister, it, Snackbar.LENGTH_SHORT
            ).show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.register)

        setClick()

        userViewModel.isLoading.observe(this){
            setProgressDialog(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setProgressDialog(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setClick() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (nameAccount.text.toString().length >= 2) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAccount.text.toString())
                            .matches()
                    ) {
                        if (passwordAccount.length() >= 8) {
                            if (passwordAccount.text.toString().isNotEmpty()) doSignUp()
                            else showSnackbar(resources.getString(R.string.password_dont_match_alert))
                        }else showSnackbar(resources.getString(R.string.password_alert))
                    }else showSnackbar(resources.getString(R.string.email_invalid))
                }else showSnackbar(resources.getString(R.string.name_alert))
            }
        }
    }

    private fun showSnackbar(isiString: String) {
        Snackbar.make(
            binding.btnRegister, isiString, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun doSignUp() {
        binding.apply {
            Log.d(
                ContentValues.TAG, "doSignUp:" + nameAccount.text.toString()
                        + emailAccount.text.toString()
            )
        }

        binding.apply {
            userViewModel.userRegister(
                RegisterUser(
                    nameAccount.text.toString(),
                    emailAccount.text.toString(),
                    passwordAccount.text.toString()
                )
            )
        }
    }
}