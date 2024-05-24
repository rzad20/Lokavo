package com.lokavo

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lokavo.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var latLng: LatLng
    private lateinit var binding: ActivityResultBinding
    private var currentMarker: Marker? = null
    private lateinit var mapFragment: SupportMapFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.result)
        }

        latLng = (if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(LOCATION, LatLng::class.java)
        } else {
            intent.getParcelableExtra(LOCATION)
        }) ?: LatLng(0.0, 0.0)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map.apply {
            uiSettings.apply {
                isScrollGesturesEnabled = false
                isZoomGesturesEnabled = false
                isRotateGesturesEnabled = false
                isZoomControlsEnabled = false
                isIndoorLevelPickerEnabled = false
                isCompassEnabled = false
                isMapToolbarEnabled = false
            }
            currentMarker = addMarker(MarkerOptions().position(latLng))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> false
        }
    }

    companion object {
        const val LOCATION = "location"
    }
}