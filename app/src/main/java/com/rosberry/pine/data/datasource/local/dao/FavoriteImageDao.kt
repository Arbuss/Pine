package com.rosberry.pine.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rosberry.pine.data.datasource.local.entity.FavoriteImageEntity

@Dao
interface FavoriteImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FavoriteImageEntity)

    @Delete
    fun delete(item: FavoriteImageEntity)

    @Query("select * from FavoriteImage order by timestamp desc")
    fun getAll(): List<FavoriteImageEntity>

    @Query("select * from FavoriteImage where id = :id")
    fun get(id: String): List<FavoriteImageEntity>
}