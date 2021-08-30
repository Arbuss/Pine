package com.rosberry.pine.data.repository.model

import androidx.annotation.Keep

@Keep
data class DTOUrls(
        val raw: String,
        val regular: String,
        val thumb: String
)