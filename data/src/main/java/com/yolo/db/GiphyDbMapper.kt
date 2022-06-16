package com.yolo.db

import com.yolo.data.GiphyItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiphyDbMapper @Inject constructor() : DbMapper<GiphyDbDto, GiphyItem> {
    override fun fromDto(dto: GiphyDbDto): GiphyItem {
        return GiphyItem(id = dto.id, url = dto.url, username = dto.username, title = dto.title)
    }

    override fun toDto(domain: GiphyItem): GiphyDbDto? {
        return GiphyDbDto(
            id = domain.id,
            url = domain.url,
            username = domain.username,
            title = domain.title,
        )
    }
}