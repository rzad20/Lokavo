package com.lokavo.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.lokavo.data.repository.MapsRepository
import com.lokavo.data.Result
import com.lokavo.data.remote.response.DetailsItem
import com.lokavo.data.remote.response.ModelingResultsResponse

class ResultViewModel(private val repository: MapsRepository) : ViewModel() {
    private val _latLng = MutableLiveData<LatLng>(null)
    val latLng: LiveData<LatLng> get() = _latLng
    private val _markers = MutableLiveData<MutableList<Marker>>(null)
    val markers: LiveData<MutableList<Marker>> get() = _markers
    fun getModelingResults(latitude: Double, longitude: Double): LiveData<Result<ModelingResultsResponse>?> {
        return repository.getModelingResults(latitude, longitude)
    }

    fun getPlaceDetail(placeId: String): LiveData<Result<DetailsItem>?> {
        return repository.getPlaceDetail(placeId)
    }

    fun setLatLng(latLng: LatLng) {
        _latLng.value = latLng
    }

    fun setMarkers(markers: MutableList<Marker>) {
        _markers.value = markers
    }
}