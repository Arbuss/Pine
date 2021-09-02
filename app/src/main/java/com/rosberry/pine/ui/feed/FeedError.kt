package com.rosberry.pine.ui.feed

sealed class FeedError : Exception() {
    class NoConnection : FeedError()

    class ServerError : FeedError()

    class NothingFound : FeedError()

    class NoError : FeedError()
}
