package com.yolo.db.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yolo.db.GiphyDbDto

@Database(entities = [GiphyDbDto::class], version = 1)
abstract class GiphyDatabase : RoomDatabase() {
    abstract fun giphyItemDao(): GiphyItemDao
}