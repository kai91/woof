package com.kai.woof.di

import com.kai.woof.repository.DogRepository
import com.kai.woof.repository.DogRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DogRepositoryModule {

    @Binds
    abstract fun bindDogRepository(impl: DogRepositoryImpl): DogRepository
}