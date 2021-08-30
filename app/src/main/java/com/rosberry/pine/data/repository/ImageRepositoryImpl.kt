package com.rosberry.pine.data.repository

import com.rosberry.pine.data.datasource.remote.RemoteDataSource
import com.rosberry.pine.data.repository.model.DTOImage
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) : ImageRepository {

    override suspend fun getPage(page: Int, pageLength: Int): List<DTOImage> {
        return remoteDataSource.getPage(page, pageLength)
    }
}