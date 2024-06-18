package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClusterInterpretation(

    @field:SerializedName("A")
    val a: String? = null,

    @field:SerializedName("B")
    val b: String? = null,

    @field:SerializedName("C")
    val c: String? = null
): Parcelable
