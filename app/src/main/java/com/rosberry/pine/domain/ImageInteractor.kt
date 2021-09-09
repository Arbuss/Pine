package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.RepositoryError
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.ui.image.ImageError
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class ImageInteractor @Inject constructor(
        private val imageRepository: ImageRepository,
        private val connectionInfoInteractor: ConnectionInfoInteractor
) {

    suspend fun getPage(page: Int, pageLength: Int): Resource<List<Image>> {
        return if (connectionInfoInteractor.isInternetAvailable()) {
            try {
                val items = imageRepository.getPage(page, pageLength)
                if (items.isNotEmpty()) {
                    Resource.Success(items)
                } else {
                    Resource.Error(ImageError.NothingFound())
                }
            } catch (serverError: RepositoryError.ServerError) {
                Resource.Error(ImageError.ServerError())
            } catch (nothingFount: RepositoryError.NothingFound) {
                Resource.Error(ImageError.NothingFound())
            }
        } else {
            Resource.Error(ImageError.NoConnection())
        }
    }
}