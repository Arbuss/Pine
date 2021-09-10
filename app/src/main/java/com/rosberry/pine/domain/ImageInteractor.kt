package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class ImageInteractor @Inject constructor(
        private val imageRepository: ImageRepository,
        connectionInfoInteractor: ConnectionInfoInteractor
) : BaseInteractor(connectionInfoInteractor) {

    suspend fun getPage(page: Int, pageSize: Int): Resource<List<Image>> = try {
        isInternetAvailable(page)
        responseHandler(imageRepository.getPage(page, pageSize))
    } catch (e: Exception) {
        getError(e)
    }
}