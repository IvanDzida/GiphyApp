package com.yolo.db.core

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yolo.db.GiphyDbDto

@Dao
interface GiphyItemDao {

    @Query("SELECT * FROM gifs")
    fun getAll(): List<GiphyDbDto>

    @Query("SELECT * FROM gifs WHERE id = :id")
    fun getItem(id: String): GiphyDbDto

    @Delete
    fun deleteItem(item: GiphyDbDto)

    @Insert
    fun insertItem(item: GiphyDbDto)
}