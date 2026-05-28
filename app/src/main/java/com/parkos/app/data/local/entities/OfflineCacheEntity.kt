package com.parkos.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_cache")
data class OfflineCacheEntity(
    @PrimaryKey
    val cacheKey: String,
    val json: String,
    val cachedAt: Long
)