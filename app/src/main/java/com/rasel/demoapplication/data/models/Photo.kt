package com.rasel.demoapplication.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @Json(name = "photographer_url")
    val photographerUrl: String,
    @Json(name = "photographer_id")
    val photographerId: Int,
    @Json(name = "avg_color")
    val avgColor: String,
    val src: Src
)