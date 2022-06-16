package com.yolo.giphyapp.api

data class UploadResponse(
    val data: Data,
    val meta: Meta,
)

data class Data(
    val id: String,
)

data class Meta(
    val msg: String,
    val status: Int,
)