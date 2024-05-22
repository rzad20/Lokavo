package com.lokavo

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.lokavo.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var currentMarker: Marker? = null

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
    }

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
        val autocompleteSupportFragment =
            (childFragmentManager.findFragmentById(R.id.fragment) as AutocompleteSupportFragment).setPlaceFields(
                listOf(Place.Field.LAT_LNG, Place.Field.NAME)
            )
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    mapFragment.getMapAsync { googleMap ->
                        currentMarker?.remove()
                        currentMarker =
                            googleMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    }
                }
            }

            override fun onError(status: Status) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${status.statusMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}