package com.parkos.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.parkos.app.data.local.dao.OfflineCacheDao
import com.parkos.app.data.local.entities.OfflineCacheEntity

@Database(
    entities = [
        OfflineCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ParkosDatabase : RoomDatabase() {
    abstract fun offlineCacheDao(): OfflineCacheDao
}