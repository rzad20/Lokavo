package com.lokavo.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker

class MapsViewModel : ViewModel() {
    private val _isShow = MutableLiveData(false)
    val isShow: LiveData<Boolean> get() = _isShow
    private val _currentMarker = MutableLiveData<Marker?>(null)
    val currentMarker: LiveData<Marker?> get() = _currentMarker

    fun setIsShow(value: Boolean) {
        _isShow.value = value
    }

    fun setCurrentMarker(marker: Marker?) {
        _currentMarker.value = marker
    }
}