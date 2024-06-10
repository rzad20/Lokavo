package com.lokavo.data.remote.response

import com.google.gson.annotations.SerializedName

data class ReviewsPerRating(

    @field:SerializedName("1")
    val one: Int? = null,

    @field:SerializedName("2")
    val two: Int? = null,

    @field:SerializedName("3")
    val three: Int? = null,

    @field:SerializedName("4")
    val four: Int? = null,

    @field:SerializedName("5")
    val five: Int? = null
)