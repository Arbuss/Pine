package com.rosberry.pine.data.datasource.remote.usplash.model

import androidx.annotation.Keep

@Keep
data class NTOUrls(
        val raw: String,
        val regular: String,
        val thumb: String
)