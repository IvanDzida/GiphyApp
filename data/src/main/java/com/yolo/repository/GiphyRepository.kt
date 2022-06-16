package com.yolo.repository

import com.yolo.data.GiphyItem

interface GiphyRepository {
    suspend fun getTrending(offset: Int, limit: Int): List<GiphyItem>
    suspend fun search(query: String, offset: Int, limit: Int): List<GiphyItem>
}