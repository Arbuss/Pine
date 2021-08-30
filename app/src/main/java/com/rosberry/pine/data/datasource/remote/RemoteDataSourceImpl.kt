package com.rosberry.pine.data.datasource.remote

import com.rosberry.pine.data.datasource.remote.usplash.UnsplashApi
import com.rosberry.pine.data.datasource.remote.usplash.model.NTOImage
import com.rosberry.pine.data.datasource.remote.usplash.model.NTOUrls
import com.rosberry.pine.data.repository.model.DTOImage
import com.rosberry.pine.data.repository.model.DTOUrls
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RemoteDataSourceImpl @Inject constructor(private val api: UnsplashApi) : RemoteDataSource {

    override suspend fun getPage(page: Int, pageLength: Int): List<DTOImage> = suspendCoroutine { continuation ->
        val response = api.getPage(page, pageLength).execute()
        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
            continuation.resume(response.body()!!.map { ntoImage: NTOImage -> ntoImage.toDTO() })
        } else {
            continuation.resume(emptyList())
        }
    }

    private fun NTOImage.toDTO(): DTOImage = DTOImage(id, description, urls.toDTO())
    private fun NTOUrls.toDTO(): DTOUrls = DTOUrls(raw, regular, thumb)
}