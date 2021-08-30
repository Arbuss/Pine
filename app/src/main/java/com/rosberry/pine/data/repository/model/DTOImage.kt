package com.rosberry.pine.data.repository.model

import androidx.annotation.Keep

@Keep
data class DTOImage(
        val id: String,
        val description: String,
        val urls: DTOUrls
)