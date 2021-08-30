package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.model.DTOImage

interface ImageInteractor {

    suspend fun getPage(page: Int, pageLength: Int): List<DTOImage>
}