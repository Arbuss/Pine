package com.rosberry.pine.data.datasource.remote.usplash

import com.rosberry.pine.data.repository.model.Image
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApi {

    @GET("/photos")
    suspend fun getPage(
            @Query("page") page: Int,
            @Query("per_page") pageLength: Int
    ): Call<List<Image>>
}