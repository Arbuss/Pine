package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.remote.unsplash.PhotosApi
import com.rosberry.pine.data.repository.model.Image
import retrofit2.Response
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: PhotosApi) {

    suspend fun getPage(page: Int, pageLength: Int): List<Image> {
        return handleResponse(api.getPage(page, pageLength))
    }

    suspend fun searchPage(query: String, page: Int, pageSize: Int): List<Image> {
        return handleResponse(api.searchPage(query, page, pageSize)).results
    }

    private fun <T> handleResponse(response: Response<T>): T {
        if (!response.isSuccessful) {
            throw handleError(response.code())
        }

        return response.body()!!
    }

    private fun <T> handleResponse(response: Response<List<T>>): List<T> {
        if (!response.isSuccessful) {
            throw handleError(response.code())
        }

        return response.body()!!
    }

    private fun handleError(errorCode: Int) = when (errorCode) {
        PhotosApi.SERVER_ERROR_500, PhotosApi.SERVER_ERROR_503 -> {
            RepositoryError.ServerError()
        }
        PhotosApi.NOT_FOUND -> {
            RepositoryError.NothingFound()
        }
        else -> {
            RepositoryError.UnknownError()
        }
    }
}