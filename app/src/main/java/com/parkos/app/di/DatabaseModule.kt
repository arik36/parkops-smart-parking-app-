package com.parkos.app.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.parkos.app.data.local.AppDatabase
import com.parkos.app.data.local.dao.OfflineCacheDao
import com.parkos.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "parkos_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideUserDao(
        db: AppDatabase
    ): UserDao = db.userDao()

    @Provides
    fun provideOfflineCacheDao(
        db: AppDatabase
    ): OfflineCacheDao = db.offlineCacheDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}