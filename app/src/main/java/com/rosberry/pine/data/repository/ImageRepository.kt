package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.local.AppDatabase
import com.rosberry.pine.data.datasource.local.entity.FavoriteImageEntity
import com.rosberry.pine.data.datasource.local.entity.SearchCacheEntity
import com.rosberry.pine.data.datasource.remote.unsplash.PhotosApi
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.data.repository.model.Urls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: PhotosApi, private val database: AppDatabase) {

    suspend fun getPage(page: Int, pageLength: Int): List<Image> {
        return handleResponse(api.getPage(page, pageLength)).map { image ->
            if (isImageLiked(image.id)) { // TODO сомнительное с точки зрения производительности место. Посоветоваться
                image.copy(isLiked = true)
            } else {
                image
            }
        }
    }

    suspend fun searchPage(query: String, page: Int, pageSize: Int): List<Image> {
        database.searchCacheDao()
            .insert(SearchCacheEntity(query, System.currentTimeMillis()))
        return handleResponse(api.searchPage(query, page, pageSize)).results
    }

    suspend fun getLastSearchQueries(count: Int): List<String> {
        return database.searchCacheDao()
            .getLastSearchQueries(count)
            .map { it.query }
    }

    suspend fun getAllLikedImages(): List<Image> {
        return database.favoriteImageDao()
            .getAll()
            .map { it.toImage() }
    }

    suspend fun getAllLikedImagesInFlow(): Flow<List<Image>> {
        return database.favoriteImageDao()
            .getAllInFlow()
            .map { it.map { it.toImage() } }
    }

    suspend fun likeImage(image: Image) {
        database.favoriteImageDao()
            .insert(image.toEntity(System.currentTimeMillis()))
    }

    suspend fun unlikeImage(image: Image) {
        database.favoriteImageDao()
            .delete(image.toEntity(System.currentTimeMillis()))
    }

    private suspend fun isImageLiked(id: String): Boolean {
        val dbItem = database.favoriteImageDao().get(id)
        return dbItem.isNotEmpty()
    }

    private fun <T> handleResponse(response: Response<T>): T {
        if (!response.isSuccessful) {
            throw handleError(response.code())
        }
        if (response.body() == null) {
            throw RepositoryError.NothingFound()
        }

        return response.body()!!
    }

    private fun <T> handleResponse(response: Response<List<T>>): List<T> {
        if (!response.isSuccessful) {
            throw handleError(response.code())
        }
        if (response.body() == null || response.body()
                    ?.isEmpty() == true) {
            throw RepositoryError.NothingFound()
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

    private fun Image.toEntity(timestamp: Long): FavoriteImageEntity =
            FavoriteImageEntity(id, blurHash, urls.full, urls.small,
                    description, width, height, timestamp)

    private fun FavoriteImageEntity.toImage(): Image = Image(id, description,
            Urls("", fullImageUrl, "", smallImageUrl, ""),
            width, height, blurhash, isLiked = true)
}