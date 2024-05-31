package com.lokavo.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.lokavo.BuildConfig
import com.lokavo.R
import com.lokavo.data.local.entity.History
import com.lokavo.databinding.FragmentMapsBinding
import com.lokavo.ui.result.ResultActivity
import com.lokavo.ui.history.HistoryViewModel
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
    private lateinit var googleMap: GoogleMap
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment
    private lateinit var geocoder: Geocoder
    private lateinit var mapFragment: SupportMapFragment
    private val historyViewModel: HistoryViewModel by viewModel()
    private val mapsViewModel: MapsViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.API_KEY)
        }

        mapsViewModel.isShow.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                binding.btnChoose.visibility = View.VISIBLE
            } else {
                binding.btnChoose.visibility = View.GONE
            }
        }

        setupAutocompleteSupportFragment()
        setupMapFragment()
        setupAutocompleteEditText()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        handleMyLocationButtonClick()
        handleChooseButtonClick()
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
                    historyViewModel.insertOrUpdate(
                        History(
                            latitude = lat,
                            longitude = long,
                            address = addressName,
                            date = DateFormatter.getCurrentDate()
                        )
                    )
                }

                val intent = Intent(context, ResultActivity::class.java)
                intent.putExtra(ResultActivity.LOCATION, currentMarker?.position)
                startActivity(intent)
            }
        }
    }

    private fun setupAutocompleteSupportFragment() {
        autocompleteSupportFragment =
            (childFragmentManager.findFragmentById(R.id.fragment) as AutocompleteSupportFragment).apply {
                setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))
            }

        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    mapFragment.getMapAsync { googleMap ->
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
            }

            override fun onError(status: Status) {}
        })
    }

    private fun setupMapFragment() {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)
    }

    private fun setupAutocompleteEditText() {
        val autoCompleteEditText = autocompleteSupportFragment
            .view?.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        autoCompleteEditText?.hint = getString(R.string.cari_lokasi)

        autoCompleteEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    currentMarker?.remove()
                    binding.btnChoose.visibility = View.GONE
                }
            }
        })
    }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = false
                isIndoorLevelPickerEnabled = false
                isCompassEnabled = false
                isMapToolbarEnabled = false
            }
        }

        getMyLocation()

        googleMap.setOnMapLongClickListener { latLng ->
            handleMapLongClick(latLng)
        }

        googleMap.setOnPoiClickListener { poi ->
            handlePoiClick(poi)
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
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng, 13f
                            ),
                            1000,
                            null
                        )
                    }
                }
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
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

    override fun onDestroyView() {
        mapsViewModel.setIsShow(binding.btnChoose.visibility == View.VISIBLE)
        super.onDestroyView()
        _binding = null
    }
}