package com.lokavo.ui.result

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.lokavo.R
import com.lokavo.data.Result
import com.lokavo.databinding.ActivityResultBinding
import com.lokavo.ui.detailAnalysis.DetailAnalysisActivity
import com.lokavo.utils.bitmapFromVector
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection
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

        binding.btnCloseResult.setOnClickListener {
            binding.cvResult.visibility = View.GONE
        }

        binding.btnCloseDetail.setOnClickListener {
            binding.cvDetail.visibility = View.GONE
        }

        getNearbyPlaces(latLng)
        binding.btnNavToResult.setOnClickListener {
            startActivity(Intent(this,DetailAnalysisActivity::class.java))
        }
    }

    private fun getNearbyPlaces(latLng: LatLng) {
        viewModel.getPlaces(latLng.latitude, latLng.longitude).observe(this) { res ->
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
                binding.progress.visibility = View.GONE
                return@observe
            }
            if (res != null) {
                when (res) {
                    is Result.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        Snackbar.make(binding.root, res.error, Snackbar.LENGTH_LONG).show()
                        binding.progress.visibility = View.GONE
                    }

                    is Result.Empty -> {
                        Snackbar.make(binding.root, R.string.not_found, Snackbar.LENGTH_LONG)
                            .show()
                        binding.progress.visibility = View.GONE
                    }

                    is Result.Success -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val builder = LatLngBounds.builder()
                            res.data.forEach { place ->
                                val placeLatLng = LatLng(
                                    place.coordinates?.latitude ?: 0.0,
                                    place.coordinates?.longitude ?: 0.0
                                )
                                withContext(Dispatchers.Main) {
                                    val marker = googleMap.addMarker(
                                        MarkerOptions()
                                            .position(placeLatLng)
                                            .icon(this@ResultActivity.bitmapFromVector(R.drawable.ic_pin_point_blue))
                                    )
                                    if (marker != null) {
                                        marker.tag = place.placeId
                                    }
                                }
                                builder.include(placeLatLng)
                            }

                            val bounds = builder.build()
                            val padding = 50
                            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

                            withContext(Dispatchers.Main) {
                                googleMap.animateCamera(cu)
                                currentMarker?.remove()
                                currentMarker = googleMap.addMarker(
                                    MarkerOptions().position(latLng)
                                        .icon(this@ResultActivity.bitmapFromVector(R.drawable.ic_pin_point_red))
                                )
                                googleMap.setOnMarkerClickListener { marker ->
                                    if (marker == currentMarker) {
                                        binding.clDetail.visibility = View.GONE
                                        binding.cvDetail.visibility = View.GONE
                                        binding.cvResult.visibility = View.VISIBLE
                                    } else {
                                        googleMap.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                marker.position,
                                                14.0f
                                            )
                                        )
                                        binding.clDetail.visibility = View.GONE
                                        binding.cvDetail.visibility = View.GONE
                                        binding.cvResult.visibility = View.GONE
                                        val placeId = marker.tag as? String
                                        if (placeId != null) {
                                            if (!this@ResultActivity.isOnline()) {
                                                binding.root.showSnackbarOnNoConnection(this@ResultActivity)
                                            } else {
                                                getDetail(placeId)
                                            }
                                        }
                                    }
                                    true
                                }
                                binding.progress.visibility = View.GONE
                                binding.cvResult.visibility = View.VISIBLE

                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDetail(placeId: String) {
        viewModel.getPlaceDetail(placeId).observe(this@ResultActivity) { result ->
            binding.cvDetail.visibility = View.VISIBLE
            when (result) {
                is Result.Loading -> {
                    binding.progressDetail.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    result.data.let {
                        binding.tvName.text = it.name
                        binding.tvRating.text = it.rating.toString()
                        binding.tvReview.text = getString(R.string.ulasan, it.reviews)
                        binding.tvCategory.text = it.mainCategory


                        if (binding.cvResult.visibility == View.GONE) {
                            binding.progressDetail.visibility = View.GONE
                            binding.clDetail.visibility = View.VISIBLE
                            Glide.with(this@ResultActivity).load(it.featuredImage)
                                .into(binding.ivDetail)
                        } else {
                            binding.progressDetail.visibility = View.GONE
                            binding.clDetail.visibility = View.GONE
                            binding.cvDetail.visibility = View.GONE
                        }
                    }
                }

                is Result.Error -> {
                    binding.progressDetail.visibility = View.GONE
                    binding.clDetail.visibility = View.GONE
                    binding.cvDetail.visibility = View.GONE
                    Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG).show()
                }

                is Result.Empty -> {
                    binding.progressDetail.visibility = View.GONE
                    binding.clDetail.visibility = View.GONE
                    binding.cvDetail.visibility = View.GONE
                    Snackbar.make(binding.root, R.string.not_found, Snackbar.LENGTH_LONG).show()
                }

                null -> TODO()
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
            currentMarker = addMarker(
                MarkerOptions().position(latLng)
                    .icon(this@ResultActivity.bitmapFromVector(R.drawable.ic_pin_point_red))
            )
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