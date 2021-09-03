package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class ImageInteractor @Inject constructor(private val imageRepository: ImageRepository) {

    suspend fun getPage(page: Int, pageLength: Int): Resource<List<Image>> =
            Resource.Success(imageRepository.getPage(page, pageLength))
}