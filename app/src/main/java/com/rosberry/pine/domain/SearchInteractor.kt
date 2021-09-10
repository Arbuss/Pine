package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.ImageRepository
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class SearchInteractor @Inject constructor(
        private val imageRepository: ImageRepository,
        connectionInfoInteractor: ConnectionInfoInteractor
) : BaseImageInteractor(connectionInfoInteractor) {

    suspend fun getSearchResult(query: String, page: Int, pageSize: Int): Resource<List<Image>> = try {
        isInternetAvailable(page)
        responseHandler(imageRepository.searchPage(query, page, pageSize))
    } catch (e: Exception) {
        getError(e)
    }
}