package com.agilizzulhaq.storyapplicationsubmission.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.agilizzulhaq.storyapplicationsubmission.R
import com.agilizzulhaq.storyapplicationsubmission.databinding.ActivitySignupBinding
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.SignupViewModel
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val signupViewModel: SignupViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        binding.moveLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val name = binding.editSignupName.text.toString()
            val email = binding.editSignupEmail.text.toString()
            val password = binding.editSignupPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.editSignupName.error = getString(R.string.input_name)
                }

                email.isEmpty() -> {
                    binding.editSignupEmail.error = getString(R.string.input_email)
                }

                password.isEmpty() -> {
                    binding.editSignupPassword.error = getString(R.string.input_password)
                }

                password.length < 8 -> {
                    binding.editSignupPassword.error = getString(R.string.label_validation_password)
                }

                else -> {
                    signupViewModel.signup(name, email, password).observe(this) { result ->
                        if (result.message == "201") {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle(R.string.information)
                            builder.setMessage(R.string.validate_signup_success)
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                alertDialog.dismiss()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 2000)
                        }
                        if (result.message == "400") {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle(R.string.information)
                            builder.setMessage(R.string.validate_signup_failed)
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                alertDialog.dismiss()
                            }, 2000)
                        }

                        if (result.message == "") {
                            showLoading(true)
                        } else {
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvSignup, View.ALPHA, 1f).setDuration(100)
        val name = ObjectAnimator.ofFloat(binding.tvSignupName, View.ALPHA, 1f).setDuration(100)
        val nameLayout = ObjectAnimator.ofFloat(binding.tlSignupName, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.tvSignupEmail, View.ALPHA, 1f).setDuration(100)
        val emailLayout = ObjectAnimator.ofFloat(binding.tlSignupEmail, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.tvSignupPassword, View.ALPHA, 1f).setDuration(100)
        val passwordLayout = ObjectAnimator.ofFloat(binding.tlSignupPassword, View.ALPHA, 1f).setDuration(100)
        val moveSignup = ObjectAnimator.ofFloat(binding.moveLogin, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)

        val animation = AnimatorSet().apply {
            play(title)
            play(name).after(title)
            play(nameLayout).after(name)
            play(email).after(nameLayout)
            play(emailLayout).after(email)
            play(password).after(emailLayout)
            play(passwordLayout).after(password)
            play(moveSignup).after(passwordLayout)
            play(signup).after(moveSignup)
        }

        AnimatorSet().apply {
            playSequentially(animation)
            duration = 250
            startDelay = 500
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}