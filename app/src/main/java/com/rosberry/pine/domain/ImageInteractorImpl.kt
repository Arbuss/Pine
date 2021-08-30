package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.model.DTOImage
import javax.inject.Inject

class ImageInteractorImpl @Inject constructor(private val imageRepository: ImageRepository) : ImageInteractor {

    override suspend fun getPage(page: Int, pageLength: Int): List<DTOImage> =
            imageRepository.getPage(page, pageLength)
}