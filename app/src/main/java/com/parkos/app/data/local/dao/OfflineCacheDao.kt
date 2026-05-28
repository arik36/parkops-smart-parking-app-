package com.parkos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.parkos.app.data.local.entities.OfflineCacheEntity

@Dao
interface OfflineCacheDao {

    @Upsert
    suspend fun upsertCache(entity: OfflineCacheEntity)

    @Query("select * from offline_cache where cacheKey = :cacheKey limit 1")
    suspend fun getCache(cacheKey: String): OfflineCacheEntity?

    @Query("delete from offline_cache where cacheKey = :cacheKey")
    suspend fun deleteCache(cacheKey: String)

    @Query("delete from offline_cache")
    suspend fun clearAll()
}