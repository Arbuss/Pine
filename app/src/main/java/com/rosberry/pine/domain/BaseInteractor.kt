package com.rosberry.pine.domain

import com.rosberry.pine.data.repository.RepositoryError
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.ui.image.ImageError
import com.rosberry.pine.util.Resource

open class BaseInteractor(private val connectionInfoInteractor: ConnectionInfoInteractor) {

    protected fun isInternetAvailable(page: Int) = if (!connectionInfoInteractor.isInternetAvailable()) {
        if (page == 1) {
            throw ImageError.NoConnection()
        } else {
            throw ImageError.NoConnectionWithPagination()
        }
    } else {
        true
    }

    protected fun responseHandler(items: List<Image>): Resource<List<Image>> {
        if (items.isEmpty()) {
            throw RepositoryError.NothingFound()
        }
        return Resource.Success(items)
    }

    protected fun errorHandler(exception: Throwable): Throwable = when (exception) {
        is RepositoryError.NothingFound -> {
            ImageError.NothingFound()
        }
        is RepositoryError.ServerError -> {
            ImageError.ServerError()
        }
        is RepositoryError.UnknownError -> {
            ImageError.ServerError()
        }
        else -> {
            exception
        }
    }

    protected fun getError(exception: Throwable): Resource<List<Image>> = Resource.Error(errorHandler(exception))
}