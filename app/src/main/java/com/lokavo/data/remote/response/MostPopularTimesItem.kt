package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MostPopularTimesItem(

    @field:SerializedName("time_label")
    val timeLabel: String? = null,

    @field:SerializedName("average_popularity")
    val averagePopularity: Double? = null,

    @field:SerializedName("hour_of_day")
    val hourOfDay: Int? = null
): Parcelable
