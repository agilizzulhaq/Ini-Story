package com.agilizzulhaq.storyapplicationsubmission.ui.activity

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.agilizzulhaq.storyapplicationsubmission.R
import com.agilizzulhaq.storyapplicationsubmission.databinding.ActivityAddStoryBinding
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.LoginViewModel
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.MainViewModel
import com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel.ViewModelFactory
import com.agilizzulhaq.storyapplicationsubmission.utils.getAddressName
import com.agilizzulhaq.storyapplicationsubmission.utils.reduceFileImage
import com.agilizzulhaq.storyapplicationsubmission.utils.rotateBitmap
import com.agilizzulhaq.storyapplicationsubmission.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lon = 0.0
    private var lat = 0.0

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.navy)
        supportActionBar?.let { actionBar ->
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.navy)))
            val titleString = getString(R.string.add_story)
            val title = SpannableString(titleString)
            title.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.setSpan(AbsoluteSizeSpan(24, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            supportActionBar?.title = title
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnAddCamera.setOnClickListener { startCameraX() }
        binding.btnAddGallery.setOnClickListener { startGallery() }
        binding.btnSetLocation.setOnClickListener {
            lon = mainViewModel.cordLon.value.toString().toDouble()
            lat = mainViewModel.cordLat.value.toString().toDouble()
            val location = getAddressName(this@AddStoryActivity, lat, lon).toString()
            binding.tvLocation.text = location
        }
        binding.btnAddUpload.setOnClickListener {
            if (getFile != null) {
                if (binding.editAddDescription.text.toString().isNotEmpty()) {
                    val file = reduceFileImage(getFile as File)
                    loginViewModel.getUser().observe(this) { user ->
                        mainViewModel.addStory(user.token, file, binding.editAddDescription.text.toString(), lon.toString(), lat.toString()).observe(this) { result ->
                            if (result.message == "") {
                                showLoading(true)
                            } else {
                                showLoading(false)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@AddStoryActivity, getString(R.string.empty_description_warning), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@AddStoryActivity, getString(R.string.empty_image_warning), Toast.LENGTH_SHORT).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.tvAddImage.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile

            binding.tvAddImage.setImageURI(selectedImg)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    postCoordinate(location.latitude, location.longitude)

                } else {
                    postCoordinate(0.0, 0.0)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun postCoordinate(latitude: Double, longitude: Double) {
        mainViewModel.cordLat.postValue(latitude)
        mainViewModel.cordLon.postValue(longitude)
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
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}