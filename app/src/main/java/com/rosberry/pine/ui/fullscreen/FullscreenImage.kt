package com.rosberry.pine.ui.fullscreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FullscreenImage(
        val id: String,
        val fullImageUrl: String,
        val thumbImageUrl: String,
        val rawImageUrl: String,
        val description: String?
) : Parcelable
