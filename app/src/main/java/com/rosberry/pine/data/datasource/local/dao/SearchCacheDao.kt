package com.rosberry.pine.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rosberry.pine.data.datasource.local.entity.SearchCacheEntity

@Dao
interface SearchCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: SearchCacheEntity)

    @Query("select * from SearchCache order by timestamp desc limit :limit")
    fun getNLastItems(limit: Int): List<SearchCacheEntity>
}