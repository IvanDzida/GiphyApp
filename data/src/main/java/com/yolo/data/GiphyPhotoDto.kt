package com.yolo.data

data class GiphyPhotoDto(
    val id: String,
    val images: GiphyImage,
    val username: String?,
    val title: String?,
) {

    data class GiphyImage(
        val original: OriginalImage,
        val fixed_height_small: OriginalImage,
    )

    data class OriginalImage(
        val url: String
    )
}
