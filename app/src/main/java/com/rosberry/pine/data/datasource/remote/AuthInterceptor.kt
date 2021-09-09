package com.rosberry.pine.data.datasource.remote

import com.rosberry.pine.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
                request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID ${BuildConfig.API_KEY}")
                    .build()
        )
    }
}