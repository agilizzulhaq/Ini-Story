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
import com.agilizzulhaq.storyapplicationsubmission.databinding.ActivityLoginBinding
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.LoginViewModel
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        binding.moveSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
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
        binding.btnLogin.setOnClickListener {
            val email = binding.editLoginEmail.text.toString()
            val password = binding.editLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.editLoginEmail.error = getString(R.string.input_name)
                }

                password.isEmpty() -> {
                    binding.editLoginPassword.error = getString(R.string.input_password)
                }

                password.length < 8 -> {
                    binding.editLoginPassword.error = getString(R.string.label_validation_password)
                }

                else -> {
                    loginViewModel.login(email, password).observe(this) { result ->
                        val data = result.loginResult
                        if (data != null) {
                            loginViewModel.saveUser(data.name, data.userId, data.token)
                            if (result.error) {
                                if (result.message == "400") {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setTitle(R.string.information)
                                    builder.setMessage(R.string.label_invalid_email)
                                    val alertDialog: AlertDialog = builder.create()
                                    alertDialog.setCancelable(false)
                                    alertDialog.show()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        alertDialog.dismiss()
                                    }, 2000)
                                }
                                if (result.message == "401") {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setTitle(R.string.information)
                                    builder.setMessage(R.string.login_user_not_found)
                                    val alertDialog: AlertDialog = builder.create()
                                    alertDialog.setCancelable(false)
                                    alertDialog.show()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        alertDialog.dismiss()
                                    }, 2000)
                                }
                            } else {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle(R.string.information)
                                builder.setMessage(R.string.validate_login_success)
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.setCancelable(false)
                                alertDialog.show()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    alertDialog.dismiss()
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }, 2000)
                            }
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
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(100)
        val message = ObjectAnimator.ofFloat(binding.tvLoginMessage, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.tvLoginEmail, View.ALPHA, 1f).setDuration(100)
        val emailLayout = ObjectAnimator.ofFloat(binding.tlLoginEmail, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.tvLoginPassword, View.ALPHA, 1f).setDuration(100)
        val passwordLayout = ObjectAnimator.ofFloat(binding.tlLoginPassword, View.ALPHA, 1f).setDuration(100)
        val moveSignup = ObjectAnimator.ofFloat(binding.moveSignup, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)

        val animation = AnimatorSet().apply {
            play(title)
            play(message).after(title)
            play(email).after(message)
            play(emailLayout).after(email)
            play(password).after(emailLayout)
            play(passwordLayout).after(password)
            play(moveSignup).after(passwordLayout)
            play(login).after(moveSignup)
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