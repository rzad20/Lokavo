package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
): Parcelable