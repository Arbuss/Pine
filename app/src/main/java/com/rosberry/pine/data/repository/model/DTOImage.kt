package com.rosberry.pine.data.repository.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DTOImage(
        val id: String,
        val description: String?,
        val urls: DTOUrls,
        val width: Int,
        val height: Int,
        @SerializedName("blur_hash") val blurHash: String
)