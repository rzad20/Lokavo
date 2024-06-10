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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.lokavo.R
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.databinding.ActivityResultBinding
import com.lokavo.ui.detailAnalysis.DetailAnalysisActivity
import com.lokavo.utils.bitmapFromVector
import com.lokavo.utils.extractRelevantText
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
    private val markers = mutableListOf<Marker>()

    private var result : ModelingResultsResponse = ModelingResultsResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.result)
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

        binding.btnAnalyzeResult.setOnClickListener {
            currentMarker?.let { marker ->
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.position, 14f)
                googleMap.animateCamera(cameraUpdate)
                binding.clDetail.visibility = View.GONE
                binding.cvDetail.visibility = View.GONE
                binding.cvResult.visibility = View.VISIBLE
            }
        }

        viewModel.latLng.observe(this) {
            if (it == null) {
                getModelingResults(latLng)
            }
        }

        binding.btnNavToResult.setOnClickListener {
            val moveWithObjectIntent = Intent(this, DetailAnalysisActivity::class.java)
            moveWithObjectIntent.putExtra(DetailAnalysisActivity.RESULT, result)
            startActivity(moveWithObjectIntent)
        }
    }

    private fun getModelingResults(latLng: LatLng) {
        viewModel.getModelingResults(latLng.latitude, latLng.longitude).observe(this) { res ->
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
                        result = ModelingResultsResponse(
                            shortInterpretation = res.data.shortInterpretation,
                            longInterpretation = res.data.longInterpretation,
                            summaryHeader = res.data.summaryHeader,
                            clusterProportion = res.data.clusterProportion
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            val builder = LatLngBounds.builder()
                            res.data.poiMap?.forEach { place ->
                                val placeLatLng = LatLng(
                                    place?.coordinates?.latitude ?: 0.0,
                                    place?.coordinates?.longitude ?: 0.0
                                )
                                withContext(Dispatchers.Main) {
                                    val marker = googleMap.addMarker(
                                        MarkerOptions()
                                            .position(placeLatLng)
                                            .icon(this@ResultActivity.bitmapFromVector(R.drawable.ic_pin_point_blue))
                                    )
                                    if (marker != null) {
                                        marker.tag = place?.placeId
                                        markers.add(marker)
                                    }
                                }
                                builder.include(placeLatLng)
                            }

                            binding.kategoriResult.text = res.data.summaryHeader
                            val shortInterpretation = res.data.shortInterpretation?.let { extractRelevantText(it) }
                            binding.shortAnalysis.text = shortInterpretation

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
                                binding.progress.visibility = View.GONE
                                binding.cvDetail.visibility = View.GONE
                                binding.cvResult.visibility = View.VISIBLE
                                binding.btnAnalyzeResult.visibility = View.VISIBLE
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

                null -> {}
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
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@ResultActivity, R.raw.map_style))
            currentMarker = addMarker(
                MarkerOptions().position(latLng)
                    .icon(this@ResultActivity.bitmapFromVector(R.drawable.ic_pin_point_red))
            )
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
        }

        viewModel.markers.observe(this) { markers ->
            if (markers != null) {
                markers.forEach { marker ->
                    marker.position.let { position ->
                        val icon = this.bitmapFromVector(R.drawable.ic_pin_point_blue)
                        val newMarker = googleMap.addMarker(
                            MarkerOptions().position(position)
                                .icon(icon)
                        )
                        if (newMarker != null) {
                            newMarker.tag = marker.tag
                            this.markers.add(newMarker)
                        }
                    }
                }
                currentMarker?.remove()
                currentMarker = googleMap.addMarker(
                    MarkerOptions().position(latLng)
                        .icon(this@ResultActivity.bitmapFromVector(R.drawable.ic_pin_point_red))
                )
                binding.btnAnalyzeResult.visibility = View.VISIBLE
            }
        }
        googleMap.setOnMarkerClickListener { marker ->
            if (this.markers.isNotEmpty()) {
                if (marker == currentMarker) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.position, 14f)
                    googleMap.animateCamera(cameraUpdate)
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
            }
            true
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

    override fun onDestroy() {
        viewModel.setLatLng(latLng)
        viewModel.setMarkers(markers)
        super.onDestroy()
    }

    companion object {
        const val LOCATION = "location"
    }
}