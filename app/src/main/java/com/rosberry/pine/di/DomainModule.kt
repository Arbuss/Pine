package com.rosberry.pine.di

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.domain.ImageInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideImageInteractor(imageRepository: ImageRepository): ImageInteractor =
            ImageInteractorImpl(imageRepository)

}