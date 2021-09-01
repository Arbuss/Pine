package com.rosberry.pine.data.datasource.remote.usplash

import com.rosberry.pine.BuildConfig
import com.rosberry.pine.data.repository.model.Image
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {
    companion object {

        const val BAD_REQUEST = 200
        const val UNAUTHORIZED = 401
        const val NOT_FOUND = 404
        const val SERVER_ERROR_1 = 500
        const val SERVER_ERROR_2 = 503
    }

    @Headers("Authorization: Client-ID ${BuildConfig.API_KEY}")
    @GET("/photos")
    fun getPage(
            @Query("page") page: Int,
            @Query("per_page") pageLength: Int
    ): Call<List<Image>>
}