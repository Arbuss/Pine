package com.rosberry.pine.di

import com.rosberry.pine.data.datasource.remote.RemoteDataSource
import com.rosberry.pine.data.datasource.remote.RemoteDataSourceImpl
import com.rosberry.pine.data.datasource.remote.usplash.UnsplashApi
import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.ImageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(unsplashApi: UnsplashApi): RemoteDataSource =
            RemoteDataSourceImpl(unsplashApi)

    @Provides
    @Singleton
    fun provideImageRepository(remoteDataSource: RemoteDataSource): ImageRepository =
            ImageRepositoryImpl(remoteDataSource)
}