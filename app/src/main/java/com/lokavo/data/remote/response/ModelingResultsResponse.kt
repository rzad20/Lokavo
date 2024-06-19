package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelingResultsResponse(

    @field:SerializedName("summary_header")
    val summaryHeader: String? = null,

    @field:SerializedName("long_interpretation")
    val longInterpretation: String? = null,

    @field:SerializedName("cluster_proportion")
    val clusterProportion: ClusterProportion? = null,

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("poi_map")
    val poiMap: List<PoiMapItem?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("cluster_interpretation")
    val clusterInterpretation :ClusterInterpretation? = null,

    val latLng: LatLng? = null,

    val top: List<PoiMapItem?>? = null
) : Parcelable

