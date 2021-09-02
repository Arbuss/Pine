package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.remote.usplash.PhotosApi
import com.rosberry.pine.data.repository.model.Image
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: PhotosApi) {

    suspend fun getPage(page: Int, pageLength: Int): List<Image> {
        val response = api.getPage(page, pageLength)

        if (!response.isSuccessful) {
            throw errorHandling(response.code())
        }

        return response.body()!!
    }

    private fun errorHandling(errorCode: Int) = when (errorCode) {
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