package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClusterProportion(

    @field:SerializedName("A")
    val a: Int? = null,

    @field:SerializedName("B")
    val b: Int? = null,

    @field:SerializedName("C")
    val c: Int? = null
): Parcelable
