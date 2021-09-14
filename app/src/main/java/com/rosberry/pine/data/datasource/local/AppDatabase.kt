package com.rosberry.pine.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rosberry.pine.data.datasource.local.dao.SearchCacheDao
import com.rosberry.pine.data.datasource.local.entity.SearchCacheEntity

@Database(
        entities = [
            SearchCacheEntity::class
        ], exportSchema = false, version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchCacheDao(): SearchCacheDao
}