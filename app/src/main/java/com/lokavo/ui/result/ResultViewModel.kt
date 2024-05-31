package com.lokavo.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.repository.MapsRepository
import com.lokavo.data.Result
import com.lokavo.data.remote.response.PlacesItem

class ResultViewModel(private val repository: MapsRepository) : ViewModel() {
    fun getPlaces(latitude: Double, longitude: Double): LiveData<Result<List<PlacesItem>>?> {
        return repository.getNearbyPlace(latitude, longitude)
    }
    fun getPlaceDetail(placeId: String): LiveData<Result<PlacesItem>?> {
        return repository.getPlaceDetail(placeId)
    }

}