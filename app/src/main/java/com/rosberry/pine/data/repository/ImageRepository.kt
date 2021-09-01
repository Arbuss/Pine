package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.remote.usplash.PhotosApi
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: PhotosApi) {

    suspend fun getPage(page: Int, pageLength: Int): Resource<List<Image>> {
        val response = api.getPage(page, pageLength)

        return if(response.isSuccessful) {
            Resource.Success(response.body()!!)
        } else {
            Resource.Error(errorHandling(response.code()))
        }
    }

    private fun errorHandling(errorCode: Int) = when (errorCode) {
        PhotosApi.SERVER_ERROR_1, PhotosApi.SERVER_ERROR_2 -> {
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