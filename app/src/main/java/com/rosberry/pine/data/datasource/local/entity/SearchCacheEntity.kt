package com.rosberry.pine.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchCache")
data class SearchCacheEntity(
        @PrimaryKey(autoGenerate = true) val id: Long?,
        val query: String
)
