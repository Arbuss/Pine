package com.rosberry.pine.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteImage")
data class FavoriteImageEntity(
        @PrimaryKey val id: String,
        val blurhash: String,
        val fullImageUrl: String,
        val smallImageUrl: String,
        val description: String?,
        val width: Int,
        val height: Int,
        val timestamp: Long
)