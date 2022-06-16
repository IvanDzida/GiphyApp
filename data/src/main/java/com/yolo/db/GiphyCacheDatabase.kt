package com.yolo.db

import com.yolo.data.GiphyItem
import com.yolo.db.core.GiphyItemDao
import javax.inject.Inject

class GiphyCacheDatabase @Inject constructor(
    private val dao: GiphyItemDao,
    private val mapper: GiphyDbMapper,
) : GiphyCacheRepository {

    override suspend fun insertAll(gifs: List<GiphyItem>) {
        gifs.map { item ->
            mapper.toDto(item)
        }.forEach {
            dao.insertItem(it!!)
        }
    }

    // TODO - handle this
    override suspend fun insertItem(item: GiphyItem) {

    }

    override fun getGifs(): List<GiphyItem> {
        return dao.getAll().map { item ->
            mapper.fromDto(item)
        }
    }
}