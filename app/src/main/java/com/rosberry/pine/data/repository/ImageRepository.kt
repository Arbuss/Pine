package com.rosberry.pine.data.repository

import com.rosberry.pine.data.repository.model.DTOImage

interface ImageRepository {

    suspend fun getPage(page: Int, pageLength: Int): List<DTOImage>
}