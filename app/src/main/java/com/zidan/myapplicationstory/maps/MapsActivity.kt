package com.zidan.myapplicationstory.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.zidan.myapplicationstory.R
import com.zidan.myapplicationstory.databinding.ActivityMapsBinding
import com.zidan.myapplicationstory.main.UserViewModel
import com.zidan.myapplicationstory.story.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    private val storyViewModel by viewModels<StoryViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setStyle()
        setCameraMovement()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        userViewModel.getDataSession().observe(this) {
            if (it.token.trim() != "") {
                storyViewModel.getStoryWithLocation(it.token)
            }
        }

        storyViewModel.dataStoryWithLocation.observe(this) {
            if (it != null) {
                for (data in it) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(data.lat, data.lon))
                            .title(getString(R.string.poststory) + " " + data.name)
                            .snippet("ID : " + data.id)
                    )
                }
            }
        }
    }

    private fun setCameraMovement() {
        val cameraFocus = LatLng(-6.200000, 106.816666)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraFocus, 5f))
    }

    private fun setStyle() {
        try {
            val styleParsing =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!styleParsing) {
                Toast.makeText(this, R.string.parsing, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(this, R.string.failedstyle, Toast.LENGTH_SHORT).show()
            Log.e(getString(R.string.errorparsing), getString(R.string.cantfind), e)
        }
    }
}