package com.lokavo.data.remote.response

import com.google.gson.annotations.SerializedName

data class PlaceDetailsResponse(

    @field:SerializedName("details")
    val details: List<DetailsItem?>? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("message")
    val message: String? = null
)

