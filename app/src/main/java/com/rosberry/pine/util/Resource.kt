package com.rosberry.pine.util

sealed class Resource<T> {
    data class Success<T>(val item: T): Resource<T>()

    data class Error<T>(val exception: Throwable): Resource<T>()
}
