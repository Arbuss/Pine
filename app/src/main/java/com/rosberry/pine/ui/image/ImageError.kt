package com.rosberry.pine.ui.image

sealed class ImageError : Exception() {
    class NoConnection : ImageError()

    class ServerError : ImageError()

    class NothingFound : ImageError()

    class NoError : ImageError()
}
