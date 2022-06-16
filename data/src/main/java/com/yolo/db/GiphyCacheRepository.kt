package com.yolo.db

import com.yolo.data.GiphyItem

interface GiphyCacheRepository {
    suspend fun insertAll(gifs: List<GiphyItem>)
    suspend fun insertItem(item: GiphyItem)
    fun getGifs(): List<GiphyItem>
}