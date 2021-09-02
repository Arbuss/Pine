package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.remote.usplash.PhotosApi
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.util.Resource
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: PhotosApi) {

    suspend fun getPage(page: Int, pageLength: Int): List<Image> {
        val response = api.getPage(page, pageLength)

        return response.body()!!
    }
}