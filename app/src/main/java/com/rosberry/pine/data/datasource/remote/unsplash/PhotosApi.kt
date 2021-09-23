package com.rosberry.pine.data.datasource.remote.unsplash

import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.data.repository.model.SearchedImages
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApi {
    companion object {

        const val BAD_REQUEST = 200
        const val UNAUTHORIZED = 401
        const val NOT_FOUND = 404
        const val SERVER_ERROR_500 = 500
        const val SERVER_ERROR_503 = 503
    }

    @GET("/photos")
    suspend fun getPage(
            @Query("page") page: Int,
            @Query("per_page") pageLength: Int
    ): Response<List<Image>>

    @GET("/search/photos")
    suspend fun searchPage(
            @Query("query") query: String,
            @Query("page") page: Int,
            @Query("per_page") pageLength: Int
    ): Response<SearchedImages>
}