package com.lokavo.ui

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lokavo.R
import com.lokavo.data.Result
import com.lokavo.databinding.ActivityResultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var latLng: LatLng
    private lateinit var binding: ActivityResultBinding
    private var currentMarker: Marker? = null
    private lateinit var mapFragment: SupportMapFragment
    private val viewModel: ResultViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
            val color = typedValue.data
            val colorDrawable = ColorDrawable(color)
            supportActionBar?.setBackgroundDrawable(colorDrawable)
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.result)
            elevation = 0f
        }

        latLng = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(LOCATION, LatLng::class.java)
        } else {
            intent.getParcelableExtra(LOCATION)
        }) ?: LatLng(0.0, 0.0)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnClose.setOnClickListener {
            binding.cvResult.visibility = View.GONE
        }
        viewModel.getPlaces(latLng.latitude, latLng.longitude).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        binding.progress.visibility = View.GONE
                    }

                    is Result.Empty -> {
                        binding.progress.visibility = View.GONE
                    }

                    is Result.Success -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val builder = LatLngBounds.builder()
                            it.data.forEach { place ->
                                val placeLatLng = LatLng(
                                    place.coordinates?.latitude ?: 0.0,
                                    place.coordinates?.longitude ?: 0.0
                                )
                                withContext(Dispatchers.Main) {
                                    googleMap.addMarker(
                                        MarkerOptions()
                                            .position(placeLatLng)
                                            .title(place.name)
                                            .icon(
                                                BitmapDescriptorFactory.defaultMarker(
                                                    BitmapDescriptorFactory.HUE_GREEN
                                                )
                                            )
                                    )
                                }
                                builder.include(placeLatLng)
                            }
                            val bounds = builder.build()
                            val padding = 50
                            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            withContext(Dispatchers.Main) {
                                googleMap.animateCamera(cu)
                                currentMarker?.remove()
                                currentMarker =
                                    googleMap.addMarker(MarkerOptions().position(latLng))
                                binding.cvResult.visibility = View.VISIBLE
                                googleMap.setOnMarkerClickListener { marker ->
                                    if (marker == currentMarker) {
                                        binding.cvResult.visibility = View.VISIBLE
                                    } else{
                                        marker.showInfoWindow()
                                    }
                                    true
                                }
                                binding.progress.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }
        override fun onMapReady(map: GoogleMap) {
            googleMap = map.apply {
                uiSettings.apply {
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