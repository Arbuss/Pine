package com.rosberry.pine.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchCache")
data class SearchCacheEntity(
        @PrimaryKey val query: String,
        val timestamp: Long
)
