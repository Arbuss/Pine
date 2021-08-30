package com.rosberry.pine.data.datasource.remote.usplash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NTOImage(
        val id: String,
        val description: String?,
        val urls: NTOUrls,
        val width: Int,
        val height: Int,
        @SerializedName("blur_hash") val blurHash: String
)