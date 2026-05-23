package com.parkos.app.di

import com.parkos.app.data.repository.AuthRepositoryImpl
import com.parkos.app.data.repository.ParkingRepositoryImpl
import com.parkos.app.domain.repository.AuthRepository
import com.parkos.app.domain.repository.ParkingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindParkingRepository(
        impl: ParkingRepositoryImpl
    ): ParkingRepository
}