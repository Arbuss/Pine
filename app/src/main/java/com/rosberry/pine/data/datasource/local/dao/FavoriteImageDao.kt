package com.rosberry.pine.data.datasource.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rosberry.pine.data.datasource.local.entity.FavoriteImageEntity

interface FavoriteImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FavoriteImageEntity)

}