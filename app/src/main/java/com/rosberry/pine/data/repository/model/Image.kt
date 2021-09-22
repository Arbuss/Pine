package com.rosberry.pine.data.repository.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Image(
        val id: String,
        val description: String?,
        val urls: Urls,
        val width: Int,
        val height: Int,
        @SerializedName("blur_hash") val blurHash: String,
        @SerializedName("liked_by_user") val isLiked: Boolean
): Parcelable