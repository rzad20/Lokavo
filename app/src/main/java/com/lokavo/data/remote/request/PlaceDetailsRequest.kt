package com.lokavo.data.remote.request

import com.google.gson.annotations.SerializedName

data class PlaceDetailsRequest(
    @SerializedName("argplace_id")
    val argplaceId: String
)