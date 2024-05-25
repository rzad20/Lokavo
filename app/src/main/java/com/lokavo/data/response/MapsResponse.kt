package com.lokavo.data.response

import com.google.gson.annotations.SerializedName

data class MapsResponse(

    @field:SerializedName("places")
    val places: List<PlacesItem?>? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("message")
    val message: String? = null
)
data class Coordinates(

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
)

data class PlacesItem(

    @field:SerializedName("coordinates")
    val coordinates: Coordinates? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null
)