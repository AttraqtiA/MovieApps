package com.example.movieapps.model

import kotlinx.serialization.Serializable

@Serializable
data class NowPlaying(
    val dates: Dates,
    val page: Int,
    val results: List<Result>, // ini yg mau kita return
    val total_pages: Int,
    val total_results: Int
)