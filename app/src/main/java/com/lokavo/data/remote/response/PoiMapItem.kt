package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PoiMapItem(

    @field:SerializedName("cluster")
    val cluster: String? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null,

    @field:SerializedName("coordinates")
    val coordinates: Coordinates? = null
): Parcelable