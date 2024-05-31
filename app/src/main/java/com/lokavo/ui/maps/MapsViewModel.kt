package com.lokavo.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapsViewModel: ViewModel() {
    private val _isShow = MutableLiveData<Boolean>(false)
    val isShow: LiveData<Boolean> get() = _isShow

    fun setIsShow(value: Boolean) {
        _isShow.value = value
    }
}