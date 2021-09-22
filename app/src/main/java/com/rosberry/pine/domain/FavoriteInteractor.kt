package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.model.Image
import javax.inject.Inject

class FavoriteInteractor @Inject constructor(private val imageRepository: ImageRepository) {
    suspend fun like(image: Image) {
        imageRepository.likeImage(image)
    }

    suspend fun unlike(image: Image) {
        imageRepository.unlikeImage(image)
    }

    suspend fun getAllLikedImages() {
        imageRepository.getAllLikedImages()
    }
}