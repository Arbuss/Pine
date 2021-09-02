package com.rosberry.pine.data.repository

sealed class RepositoryError : Exception() {

    class ServerError : RepositoryError()

    class NothingFound : RepositoryError()

    class UnknownError : RepositoryError()
}
