package com.rosberry.pine.util

import com.rosberry.pine.BuildConfig
import okhttp3.Interceptor

class Interceptor : Interceptor {
 // TODO переместить в remote
    override fun intercept(chain: Interceptor.Chain) = chain.run {
        proceed(
                request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID ${BuildConfig.API_KEY}")
                    .build()
        )
    }
}