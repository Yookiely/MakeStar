package com.example.common.experimental.network

import com.example.common.experimental.preference.CommonPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection

internal inline val Request.authorized
    get() = if (header("Authorization") == null)
        newBuilder().addHeader("Authorization", "Bearer{${CommonPreferences.token}}").build()
    else this

object AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().authorized)
}


object CodeCorrectionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request()).run {
            if (code() == HttpURLConnection.HTTP_BAD_REQUEST)
                newBuilder().code(HttpURLConnection.HTTP_UNAUTHORIZED).build() else this
        }
}