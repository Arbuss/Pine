package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.RepositoryError
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.ui.feed.FeedError
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class SearchInteractor @Inject constructor(
        private val imageRepository: ImageRepository,
        private val connectionInfoInteractor: ConnectionInfoInteractor
) {

    suspend fun getSearchResult(query: String, page: Int, pageSize: Int): Resource<List<Image>> {
        return if (connectionInfoInteractor.isInternetAvailable()) {
            try {
                val items = imageRepository.searchPage(query, page, pageSize)
                if (items.isNotEmpty()) {
                    Resource.Success(items)
                } else {
                    Resource.Error(FeedError.NothingFound())
                }
            } catch (serverError: RepositoryError.ServerError) {
                Resource.Error(FeedError.ServerError())
            } catch (nothingFound: RepositoryError.NothingFound) {
                Resource.Error(FeedError.NothingFound())
            }
        } else {
            Resource.Error(FeedError.NoConnection())
        }
    }
}