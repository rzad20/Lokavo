package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailsItem(
    @field:SerializedName("place_id")
    val placeId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("featured_image")
    val featuredImage: String? = null,

    @field:SerializedName("reviews")
    val reviews: Int? = null,

    @field:SerializedName("main_category")
    val mainCategory: String? = null,

    @field:SerializedName("categories")
    val categories: List<String?>? = null,

    @field:SerializedName("rating")
    val rating: Double? = null,

    @field:SerializedName("reviews_per_rating")
    val reviewsPerRating: ReviewsPerRating? = null,

    @field:SerializedName("coordinates")
    val coordinates: Coordinates? = null,

    @field:SerializedName("most_popular_times")
    val mostPopularTimes: List<MostPopularTimesItem?>? = null,

    @field:SerializedName("average_hour")
    val averageHour: Double? = null,

    @field:SerializedName("std_hour")
    val stdHour: Double? = null,

    @field:SerializedName("avg_popularity")
    val avgPopularity: Double? = null,

    @field:SerializedName("top_hour_popularity")
    val topHourPopularity: Double? = null,

    @field:SerializedName("top_average_popularity")
    val topAveragePopularity: Double? = null,

    @field:SerializedName("nearest_competitor_place_id")
    val nearestCompetitorPlaceId: String? = null,

    @field:SerializedName("nearest_competitor_distance")
    val nearestCompetitorDistance: Double? = null,

    @field:SerializedName("nearest_competitor_top_hour_popularity")
    val nearestCompetitorTopHourPopularity: Double? = null,

    @field:SerializedName("nearest_competitor_top_average_popularity")
    val nearestCompetitorTopAveragePopularity: Double? = null,
): Parcelable