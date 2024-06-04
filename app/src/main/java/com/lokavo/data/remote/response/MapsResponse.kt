package com.lokavo.data.remote.response

import com.google.gson.annotations.SerializedName

data class MapsResponse(

    @field:SerializedName("places")
    val places: List<PlacesItem?>? = null,

    @field:SerializedName("details")
    val details: List<PlacesItem?>? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ReviewsPerRating(

    @field:SerializedName("1")
    val jsonMember1: Int? = null,

    @field:SerializedName("2")
    val jsonMember2: Int? = null,

    @field:SerializedName("3")
    val jsonMember3: Int? = null,

    @field:SerializedName("4")
    val jsonMember4: Int? = null,

    @field:SerializedName("5")
    val jsonMember5: Int? = null
)

data class Coordinates(

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
)

data class NearbyPlaces(

    @field:SerializedName("coordinates")
    val coordinates: Coordinates? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null
)

data class PlacesItem(

    @field:SerializedName("average_hour")
    val averageHour: Double? = null,

    @field:SerializedName("most_popular_times") // ?
    val mostPopularTimes: Any? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("rating")
    val rating: Double? = null,

    @field:SerializedName("coordinates")
    val coordinates: Coordinates? = null,

    @field:SerializedName("std_hour")
    val stdHour: Double? = null,

    @field:SerializedName("avg_popularity")
    val avgPopularity: Double? = null,

    @field:SerializedName("featured_image")
    val featuredImage: String? = null,

    @field:SerializedName("nearest_competitor_top_hour_popularity") //?
    val nearestCompetitorTopHourPopularity: Any? = null,

    @field:SerializedName("reviews")
    val reviews: Int? = null,

    @field:SerializedName("reviews_per_rating")
    val reviewsPerRating: ReviewsPerRating? = null,

    @field:SerializedName("top_hour_popularity") //?
    val topHourPopularity: Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("main_category")
    val mainCategory: String? = null,

    @field:SerializedName("nearest_competitor_distance")
    val nearestCompetitorDistance: Double? = null,

    @field:SerializedName("nearest_competitor_top_average_popularity") //?
    val nearestCompetitorTopAveragePopularity: Any? = null,

    @field:SerializedName("categories")
    val categories: List<String?>? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null,

    @field:SerializedName("top_average_popularity")
    val topAveragePopularity: Double? = null,

    @field:SerializedName("nearest_competitor_place_id")
    val nearestCompetitorPlaceId: String? = null
)