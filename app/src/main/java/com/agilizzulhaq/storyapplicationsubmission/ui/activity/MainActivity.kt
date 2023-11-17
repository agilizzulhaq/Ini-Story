package com.agilizzulhaq.storyapplicationsubmission.ui.activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.agilizzulhaq.storyapplicationsubmission.R
import com.agilizzulhaq.storyapplicationsubmission.databinding.ActivityMainBinding
import com.agilizzulhaq.storyapplicationsubmission.ui.adapter.LoadingStateAdapter
import com.agilizzulhaq.storyapplicationsubmission.ui.adapter.StoryAdapter
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.LoginViewModel
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.MainViewModel
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.navy)
        binding.btnAddStory.setColorFilter(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.let { actionBar ->
            actionBar.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.navy)))
            val titleString = getString(R.string.app_name)
            val title = SpannableString(titleString)
            title.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.setSpan(AbsoluteSizeSpan(24, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            supportActionBar?.title = title
        }

        val factory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter = StoryAdapter()

        showLoading(true)

        loginViewModel.getUser().observe(this){user->
            if (user.userId.isEmpty()){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                binding.rvListStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                mainViewModel.getStory(user.token).observe(this) {
                    Log.e("List", it.toString())
                    adapter.submitData(lifecycle, it)
                    showLoading(false)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvListStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvListStory.addItemDecoration(itemDecoration)

        binding.btnAddStory.setOnClickListener {
            val i = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                loginViewModel.logout()
            }
            R.id.action_map -> {
                Intent(this, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}