package com.rosberry.pine.data.datasource.remote

import com.rosberry.pine.data.repository.model.DTOImage

interface RemoteDataSource {

    suspend fun getPage(page: Int, pageLength: Int): List<DTOImage>
}