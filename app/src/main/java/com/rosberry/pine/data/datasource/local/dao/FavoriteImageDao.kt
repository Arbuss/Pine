package com.rosberry.pine.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rosberry.pine.data.datasource.local.entity.FavoriteImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteImageEntity)

    @Delete
    suspend fun delete(item: FavoriteImageEntity)

    @Query("select * from FavoriteImage order by timestamp desc")
    suspend fun getAll(): List<FavoriteImageEntity>

    @Query("select * from FavoriteImage order by timestamp desc")
    fun getAllInFlow(): Flow<List<FavoriteImageEntity>>

    @Query("select * from FavoriteImage where id = :id")
    suspend fun get(id: String): List<FavoriteImageEntity>
}