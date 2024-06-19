package com.lokavo.ui.maps

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.lokavo.BuildConfig
import com.lokavo.R
import com.lokavo.data.local.entity.AnalyzeHistory
import com.lokavo.databinding.FragmentMapsBinding
import com.lokavo.ui.result.ResultActivity
import com.lokavo.ui.analyzeHistory.AnalyzeHistoryViewModel
import com.lokavo.utils.DateFormatter
import com.lokavo.utils.bitmapFromVector
import com.lokavo.utils.getAddress
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var currentMarker: Marker? = null
    private val analyzeHistoryViewModel: AnalyzeHistoryViewModel by viewModel()
    private val mapsViewModel: MapsViewModel by viewModel()
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
    }
    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }
    private val autocompleteSupportFragment by lazy {
        (childFragmentManager.findFragmentById(R.id.fragment) as AutocompleteSupportFragment).apply {
            setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))
        }
    }
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkGooglePlayServices()

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.API_KEY)
        }

        setupMapFragment()
        setupAutocompleteSupportFragment()
        setupAutocompleteEditText(getString(R.string.cari_lokasi))

        handleMyLocationButtonClick()
        handleChooseButtonClick()
    }

    private val callback = OnMapReadyCallback { map ->
        binding.cardView.visibility = View.VISIBLE
        googleMap = map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = false
                isIndoorLevelPickerEnabled = false
                isCompassEnabled = false
                isMapToolbarEnabled = false
            }
            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            setOnMapLongClickListener { handleMapLongClick(it) }
            setOnPoiClickListener { handlePoiClick(it) }
        }

        mapsViewModel.isShow.observe(viewLifecycleOwner) { isShow ->
            binding.btnChoose.visibility = if (isShow) View.VISIBLE else View.GONE
        }

        mapsViewModel.currentMarker.observe(viewLifecycleOwner) { marker ->
            currentMarker?.remove()
            marker?.let {
                currentMarker = googleMap.addMarker(
                    MarkerOptions().position(it.position)
                        .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
                )
            }
        }

        getMyLocation()
    }

    private fun setupMapFragment() {
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(callback)
    }

    private fun setupAutocompleteSupportFragment() {
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    currentMarker?.remove()
                    currentMarker = googleMap.addMarker(
                        MarkerOptions().position(latLng)
                            .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
                    )
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng, 13f
                        ),
                        1000,
                        null
                    )
                    binding.btnChoose.visibility = View.VISIBLE
                }
            }

            override fun onError(status: Status) {}
        })
    }

    private fun setupAutocompleteEditText(hintt: String) {
        autocompleteSupportFragment
            .view?.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
            ?.apply {
                hint = hintt
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        if (s.isEmpty()) {
                            currentMarker?.remove()
                            mapsViewModel.setCurrentMarker(null)
                            binding.btnChoose.visibility = View.GONE
                            hint = getString(R.string.cari_lokasi)
                        }
                    }
                })
            }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            binding.btnMyLocation.visibility = View.VISIBLE
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun handleMyLocationButtonClick() {
        binding.btnMyLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng, 13f
                            ),
                            1000,
                            null
                        )
                    } else {
                        enableLocationAutomatically()
                    }
                }
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun handleChooseButtonClick() {
        binding.btnChoose.setOnClickListener {
            if (!requireContext().isOnline()) {
                binding.root.showSnackbarOnNoConnection(requireContext())
            } else {
                lifecycleScope.launch {
                    val lat = currentMarker?.position?.latitude ?: 0.0
                    val long = currentMarker?.position?.longitude ?: 0.0
                    val address = geocoder.getAddress(lat, long)
                    val addressName = address?.getAddressLine(0) ?: "${lat},${long}"
                    val userId = Firebase.auth.currentUser?.uid
                    userId.let {
                        if (it != null) {
                            analyzeHistoryViewModel.insertOrUpdate(
                                it,
                                AnalyzeHistory(
                                    userId = it,
                                    latitude = lat,
                                    longitude = long,
                                    address = addressName,
                                    date = DateFormatter.getCurrentDate()
                                )
                            )
                        }
                    }
                }

                val intent = Intent(context, ResultActivity::class.java)
                currentMarker?.position?.let {
                    intent.putExtra(ResultActivity.LOCATION, it)
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleMapLongClick(latLng: LatLng) {
        currentMarker?.remove()
        if (!requireContext().isOnline()) {
            binding.root.showSnackbarOnNoConnection(requireContext())
            binding.btnChoose.visibility = View.GONE
            currentMarker = googleMap.addMarker(
                MarkerOptions().position(latLng)
                    .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
            )
            autocompleteSupportFragment.setText("${latLng.latitude},${latLng.longitude}")
            return
        }
        currentMarker = googleMap.addMarker(
            MarkerOptions().position(latLng)
                .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, googleMap.cameraPosition.zoom
            ),
            1000,
            null
        )
        binding.btnChoose.visibility = View.VISIBLE
        lifecycleScope.launch {
            val address = geocoder.getAddress(latLng.latitude, latLng.longitude)
            val addressName =
                address?.getAddressLine(0) ?: "${latLng.latitude},${latLng.longitude}"
            autocompleteSupportFragment.setText(addressName)
        }
    }

    private fun handlePoiClick(poi: PointOfInterest) {
        currentMarker?.remove()
        if (!requireContext().isOnline()) {
            binding.root.showSnackbarOnNoConnection(requireContext())
            binding.btnChoose.visibility = View.GONE
            currentMarker = googleMap.addMarker(
                MarkerOptions().position(poi.latLng)
                    .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
            )
            autocompleteSupportFragment.setText("${poi.latLng.latitude},${poi.latLng.longitude}")
            return
        }
        currentMarker = googleMap.addMarker(
            MarkerOptions().position(poi.latLng)
                .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                poi.latLng, googleMap.cameraPosition.zoom
            ),
            1000,
            null
        )
        binding.btnChoose.visibility = View.VISIBLE
        lifecycleScope.launch {
            val address = geocoder.getAddress(poi.latLng.latitude, poi.latLng.longitude)
            val addressName =
                address?.getAddressLine(0) ?: "${poi.latLng.latitude},${poi.latLng.longitude}"
            autocompleteSupportFragment.setText(addressName)
        }
    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ -> }

    private fun enableLocationAutomatically() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resultLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    MaterialAlertDialogBuilder(requireContext()).setMessage(sendEx.message)
                        .show()
                }
            }
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                binding.cardView.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        mapsViewModel.setIsShow(binding.btnChoose.visibility == View.VISIBLE)
        mapsViewModel.setCurrentMarker(currentMarker)
        super.onDestroyView()
        _binding = null
    }
}