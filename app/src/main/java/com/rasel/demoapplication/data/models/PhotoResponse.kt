package com.rasel.demoapplication.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoResponse(
    val photos: List<Photo>,
    @Json(name = "next_page")
    val nextPage: String?,
    val page: Int,
    @Json(name = "per_page")
    val perPage: Int,
    @Json(name = "total_results")
    val totalResults: Int
)