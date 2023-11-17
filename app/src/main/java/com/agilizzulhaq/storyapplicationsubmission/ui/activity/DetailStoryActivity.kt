package com.agilizzulhaq.storyapplicationsubmission.ui.activity

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agilizzulhaq.storyapplicationsubmission.R
import com.agilizzulhaq.storyapplicationsubmission.databinding.ActivityDetailStoryBinding
import com.agilizzulhaq.storyapplicationsubmission.utils.getAddressName
import com.agilizzulhaq.storyapplicationsubmission.utils.withDateFormat
import com.bumptech.glide.Glide


class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.navy)
        supportActionBar?.let { actionBar ->
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.navy)))
            val titleString = getString(R.string.detail_story)
            val title = SpannableString(titleString)
            title.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.setSpan(AbsoluteSizeSpan(24, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            supportActionBar?.title = title
        }

        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val name = intent.getStringExtra(NAME)
        val createAt = intent.getStringExtra(CREATE_AT)
        val description = intent.getStringExtra(DESCRIPTION)
        val lon = intent.getStringExtra(LONGITUDE)!!.toDouble()
        val lat = intent.getStringExtra(LATITUDE)!!.toDouble()
        val location = getAddressName(this@DetailStoryActivity, lat, lon)

        Glide.with(binding.root.context)
            .load(photoUrl)
            .into(binding.ivDetailImage)
        binding.tvDetailName.text = name
        binding.tvDetailCreatedTime.text = createAt?.withDateFormat()
        binding.tvDetailDescription.text = description
        binding.tvDetailLocation.text = location

        if (lon == 0.0 && lat == 0.0) {
            binding.ivDetailLocation.visibility = View.INVISIBLE
        } else {
            binding.ivDetailLocation.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
        const val LONGITUDE = "lon"
        const val LATITUDE = "lat"
    }
}