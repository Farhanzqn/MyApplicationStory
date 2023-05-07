package com.zidan.myapplicationstory.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.zidan.myapplicationstory.R
import com.zidan.myapplicationstory.camera.CameraActivity
import com.zidan.myapplicationstory.databinding.ActivityStoryBinding
import com.zidan.myapplicationstory.main.MainActivity
import com.zidan.myapplicationstory.main.UserViewModel
import com.zidan.myapplicationstory.utils.reduceFileImage
import com.zidan.myapplicationstory.utils.rotateBitmap
import com.zidan.myapplicationstory.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random

@AndroidEntryPoint
class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userViewModel by viewModels<UserViewModel>()
    private val storyViewModel by viewModels<StoryViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        setupPermission()
        getToken()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.add_story)

        setClick()
    }

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
                    R.string.permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getToken() {
        userViewModel.getDataSession().observe(this) {
            if (it.token.trim() != "") {
                EXTRA_TOKEN = it.token
            }
        }
    }

    private fun setClick() {
        binding.apply {
            btnCameraX.setOnClickListener { startCameraX() }
            btnAlbum.setOnClickListener { startGallery() }
            btnPost.setOnClickListener {
                if (etDesc.text.isNotEmpty()) {
                    uploadImage()
                }

            }
            location.setOnClickListener { getLocationNow() }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_a_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding.viewImage.setImageURI(selectedImg)
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

            binding.viewImage.setImageBitmap(result)
        }
    }


    private fun uploadImage() {
        val description = binding.etDesc.text.toString()

        when {
            getFile == null -> {
                Toast.makeText(
                    this@StoryActivity,
                    R.string.please_to_choose_image,
                    Toast.LENGTH_SHORT
                ).show()
            }
            description.trim().isEmpty() -> {
                Toast.makeText(
                    this@StoryActivity,
                    R.string.okee,
                    Toast.LENGTH_SHORT
                ).show()
                binding.etDesc.error = getString(R.string.descerror)
            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val address = binding.etLocation.text.toString()
                val location = addressToCoordinate(address)
                storyViewModel.uploadStory(EXTRA_TOKEN, description, file, location)

                storyViewModel.responseUpload.observe(this) { response ->
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    if (!response.error) {
                        val intent = Intent(this@StoryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setupPermission(){
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getLocationNow()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getLocationNow()
            else -> {}
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationNow() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val addressName = getAddressName(LatLng(location.latitude, location.longitude))
                    binding.etLocation.setText(addressName)
                } else {
                    Toast.makeText(this@StoryActivity, R.string.empty_location, Toast.LENGTH_SHORT).show()
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

    private fun getAddressName(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this)
            val allAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (allAddress?.isEmpty() == true) getString(R.string.empty_address) else allAddress!![0].getAddressLine(
                0
            )
        } catch (e: Exception) {
            getString(R.string.empty_address)
        }
    }

    private fun addressToCoordinate(locationName: String): LatLng {
        return try {
            val randomLatitude = randomCoordinate()
            val randomLongitude = randomCoordinate()

            val geocoder = Geocoder(this)
            val allLocation = geocoder.getFromLocationName(locationName, 1)
            if (allLocation?.isEmpty() == true) {
                LatLng(randomLatitude, randomLongitude)
            } else {
                LatLng((allLocation?.get(0)?.latitude ?:locationName) as Double,
                    (allLocation?.get(0)?.longitude ?:locationName) as Double
                )
            }
        } catch (e: Exception) {
            LatLng(0.0, 0.0)
        }
    }

    private fun randomCoordinate(): Double {
        return Random.nextDouble(15.0, 100.0)
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private var EXTRA_TOKEN = "token"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}