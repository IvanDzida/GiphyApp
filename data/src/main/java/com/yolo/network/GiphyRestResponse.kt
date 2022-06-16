package com.yolo.network

import com.yolo.data.GiphyPhotoDto

data class GiphyRestResponse(
    val data: List<GiphyPhotoDto>,
    val pagination: GiphyPagination,
)

data class GiphyPagination(
    val total_count: Int,
    val count: Int,
    val offset: Int,
)