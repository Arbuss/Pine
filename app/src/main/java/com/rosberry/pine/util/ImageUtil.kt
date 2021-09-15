package com.rosberry.pine.util

object ImageUtil {

    fun calcImageSize(screenWidth: Int, width: Int, height: Int): Pair<Int, Int> {
        val multiplier = width / screenWidth + 1

        val imageWidth = screenWidth
        val imageHeight = height / multiplier
        return imageWidth to imageHeight
    }
}