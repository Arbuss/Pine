package com.rosberry.pine.ui.fullscreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FullscreenImage(
        val fullImageUrl: String,
        val thumbImageUrl: String,
        val description: String?
) : Parcelable
