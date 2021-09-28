package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.model.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteInteractor @Inject constructor(private val imageRepository: ImageRepository) {

    suspend fun like(image: Image) {
        imageRepository.likeImage(image)
    }

    suspend fun unlike(image: Image) {
        imageRepository.unlikeImage(image)
    }

    suspend fun getAllLikedImages(): List<Image> {
        return imageRepository.getAllLikedImages()
    }

    fun getAllLikedImagesInFlow(): Flow<List<Image>> {
        return imageRepository.getAllLikedImagesInFlow()
    }
}