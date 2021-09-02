package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.RepositoryError
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.ui.feed.FeedError
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
                Resource.Success(items)
            } catch (serverError: RepositoryError.ServerError) {
                Resource.Error(FeedError.ServerError())
            } catch (nothingFount: RepositoryError.NothingFound) {
                Resource.Error(FeedError.NothingFound())
            }
        } else {
            Resource.Error(FeedError.NoConnection())
        }
    }
}