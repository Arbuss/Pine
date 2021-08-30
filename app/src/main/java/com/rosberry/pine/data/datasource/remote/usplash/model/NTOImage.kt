package com.rosberry.pine.data.datasource.remote.usplash.model

import androidx.annotation.Keep

@Keep
data class NTOImage(
        val id: String,
        val description: String,
        val urls: NTOUrls
)