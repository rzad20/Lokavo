package com.lokavo.ui

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.Status
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
import com.lokavo.databinding.FragmentMapsBinding
import com.lokavo.utils.bitmapFromVector
import com.lokavo.utils.getAddress
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection
import kotlinx.coroutines.launch
import java.util.Locale

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var currentMarker: Marker? = null
    private var googleMap: GoogleMap? = null
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment
    private lateinit var geocoder: Geocoder
    private lateinit var mapFragment: SupportMapFragment
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

        initializePlaces()
        setupAutocompleteSupportFragment()
        setupMapFragment()
        setupAutocompleteEditText()

        binding.btnChoose.setOnClickListener {
            if (!requireContext().isOnline()) {
                binding.root.showSnackbarOnNoConnection(requireContext())
            } else {
                val intent = Intent(context, ResultActivity::class.java)
                intent.putExtra(ResultActivity.LOCATION, currentMarker?.position)
                startActivity(intent)
            }
        }
    }

    private fun initializePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.API_KEY)
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

        googleMap?.setOnMapLongClickListener { latLng ->
            handleMapLongClick(latLng)
        }

        googleMap?.setOnPoiClickListener { poi ->
            handlePoiClick(poi)
        }
    }

    private fun handleMapLongClick(latLng: LatLng) {
        currentMarker?.remove()
        if (!requireContext().isOnline()) {
            binding.root.showSnackbarOnNoConnection(requireContext())
            binding.btnChoose.visibility = View.GONE
            currentMarker = googleMap?.addMarker(
                MarkerOptions().position(latLng)
                    .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
            )
            autocompleteSupportFragment.setText("${latLng.latitude},${latLng.longitude}")
            return
        }
        currentMarker = googleMap?.addMarker(
            MarkerOptions().position(latLng)
                .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
        )
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, googleMap?.cameraPosition?.zoom ?: 13f
            ),
            1000,
            null
        )
        binding.btnChoose.visibility = View.VISIBLE
        lifecycleScope.launch {
            val address = geocoder.getAddress(latLng.latitude, latLng.longitude)
            val addressName = address?.getAddressLine(0) ?: "${latLng.latitude},${latLng.longitude}"
            autocompleteSupportFragment.setText(addressName)
        }
    }

    private fun handlePoiClick(poi: PointOfInterest) {
        currentMarker?.remove()
        if (!requireContext().isOnline()) {
            binding.root.showSnackbarOnNoConnection(requireContext())
            binding.btnChoose.visibility = View.GONE
            currentMarker = googleMap?.addMarker(
                MarkerOptions().position(poi.latLng)
                    .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
            )
            autocompleteSupportFragment.setText("${poi.latLng.latitude},${poi.latLng.longitude}")
            return
        }
        currentMarker = googleMap?.addMarker(
            MarkerOptions().position(poi.latLng)
                .icon(requireContext().bitmapFromVector(R.drawable.ic_pin_point_red))
        )
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                poi.latLng, googleMap?.cameraPosition?.zoom ?: 13f
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
        super.onDestroyView()
        _binding = null
    }
}