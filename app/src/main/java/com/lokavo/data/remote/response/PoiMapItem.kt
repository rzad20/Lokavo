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
    val coordinates: Coordinates? = null,

    @field:SerializedName("top")
    val top: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("main_category")
    val mainCategory: String? = null,

    @field:SerializedName("featured_image")
    val featuredImage: String? = null,

    @field:SerializedName("reviews")
    val reviews: Int? = null,

    @field:SerializedName("rating")
    val rating: Double? = null,
): Parcelable