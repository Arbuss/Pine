package com.rosberry.pine.data.repository.model

import androidx.annotation.Keep

@Keep
data class SearchedImages(
        val results: List<Image>
)
