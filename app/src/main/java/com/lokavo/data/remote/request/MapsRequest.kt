package com.lokavo.data.remote.request

import com.google.gson.annotations.SerializedName

data class ArgLatLong(
    val arglatitude: Double,
    val arglongitude: Double
)

data class PlaceId(
    @SerializedName("argplace_id")
    val argplaceId: String
)
