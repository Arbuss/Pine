package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.remote.usplash.UnsplashApi
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.util.Resource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageRepository @Inject constructor(private val api: UnsplashApi) {

    suspend fun getPage(page: Int, pageLength: Int): Resource<List<Image>> = suspendCoroutine { continuation ->
        val response = api.getPage(page, pageLength).execute()
        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
            continuation.resume(Resource.Success(response.body()!!))
        } else {
            continuation.resume(Resource.Error(errorHandling(response.code())))
        }
    }

    private fun errorHandling(errorCode: Int) = when (errorCode) {
        UnsplashApi.SERVER_ERROR_1, UnsplashApi.SERVER_ERROR_2 -> {
            RepositoryError.ServerError()
        }
        UnsplashApi.NOT_FOUND -> {
            RepositoryError.NothingFound()
        }
        else -> {
            RepositoryError.UnknownError()
        }
    }
}