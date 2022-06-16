package com.yolo.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiphyMapper @Inject constructor() : DtoMapper<GiphyItem, GiphyPhotoDto> {
    override fun toDomain(dto: GiphyPhotoDto): GiphyItem {
        return GiphyItem(
            id = dto.id,
            url = dto.images.original.url,
            username = dto.title,
            title = dto.username,
        )
    }
}