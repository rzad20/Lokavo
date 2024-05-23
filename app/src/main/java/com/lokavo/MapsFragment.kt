package com.lokavo

import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.lokavo.databinding.FragmentMapsBinding
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

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.API_KEY)
        }

        autocompleteSupportFragment =
            (childFragmentManager.findFragmentById(R.id.fragment) as AutocompleteSupportFragment).apply {
                setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))
            }

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    mapFragment.getMapAsync { googleMap ->
                        currentMarker?.remove()
                        currentMarker = googleMap.addMarker(MarkerOptions().position(latLng))
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng, 13f
                            ),
                            1000,
                            null
                        )
                    }
                }
            }

            override fun onError(status: Status) {}
        })
    }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isIndoorLevelPickerEnabled = false
                isCompassEnabled = false
                isMapToolbarEnabled = false
            }
        }

        googleMap?.setOnMapLongClickListener { latLng ->
            currentMarker?.remove()
            currentMarker = googleMap?.addMarker(MarkerOptions().position(latLng))
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng, googleMap?.cameraPosition?.zoom ?: 13f
                ),
                1000,
                null
            )

            lifecycleScope.launch {
                val address = geocoder.getAddress(latLng.latitude, latLng.longitude)
                val addressName =
                    address?.getAddressLine(0) ?: "${latLng.latitude},${latLng.longitude}"
                autocompleteSupportFragment.setText(addressName)
            }
        }

        googleMap?.setOnPoiClickListener { poi ->
            currentMarker?.remove()
            currentMarker = googleMap?.addMarker(MarkerOptions().position(poi.latLng))
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    poi.latLng, googleMap?.cameraPosition?.zoom ?: 13f
                ),
                1000,
                null
            )

            lifecycleScope.launch {
                val address = geocoder.getAddress(poi.latLng.latitude, poi.latLng.longitude)
                val addressName =
                    address?.getAddressLine(0) ?: "${poi.latLng.latitude},${poi.latLng.longitude}"
                autocompleteSupportFragment.setText(addressName)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}