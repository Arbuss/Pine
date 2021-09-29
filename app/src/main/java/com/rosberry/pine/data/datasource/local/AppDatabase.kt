package com.rosberry.pine.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rosberry.pine.data.datasource.local.dao.FavoriteImageDao
import com.rosberry.pine.data.datasource.local.dao.SearchCacheDao
import com.rosberry.pine.data.datasource.local.entity.FavoriteImageEntity
import com.rosberry.pine.data.datasource.local.entity.SearchCacheEntity

@Database(
        entities = [
            SearchCacheEntity::class,
            FavoriteImageEntity::class
        ], exportSchema = false, version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchCacheDao(): SearchCacheDao
    abstract fun favoriteImageDao(): FavoriteImageDao
}