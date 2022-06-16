package com.yolo.repository

import com.yolo.data.GiphyItem
import com.yolo.data.GiphyMapper
import com.yolo.db.GiphyCacheDatabase
import com.yolo.db.GiphyCacheRepository
import com.yolo.network.GiphyRestApi
import javax.inject.Inject

// TODO - fix issue with DI, then use gif from DB
class GiphyRepositoryImpl @Inject constructor(
    private val restApi: GiphyRestApi,
    //private val cache: GiphyCacheRepository,
    private val mapper: GiphyMapper
) : GiphyRepository {
    override suspend fun getTrending(offset: Int, limit: Int): List<GiphyItem> {
        val list: MutableList<GiphyItem> = mutableListOf()

        try {
            val response = restApi.getTrending(offset, limit)

            response.data.forEach { item ->
                list.add(mapper.toDomain(item))
                // insert gif in DB
                // cache.insertItem(item)
            }
        } catch (e: Exception) {
            // Some error happened, get gifs from DB
            // list.addAll(cache.getGifs())
        }

        return list
    }

    override suspend fun search(query: String, offset: Int, limit: Int): List<GiphyItem> {
        val list: MutableList<GiphyItem> = mutableListOf()

        try {
            val response = restApi.search(query, offset, limit)

            response.data.forEach { item ->
                list.add(mapper.toDomain(item))
            }
        } catch (e: Exception) {
            // Some error happened, just return empty list
        }

        return list
    }
}