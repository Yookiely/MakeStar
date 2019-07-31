package com.example.common.experimental.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

//internal inline val Request.authorized
//    get() = if (header("Authorization") == null)
//        newBuilder().addHeader("Authorization", "Bearer{${CommonPreferences.token}}").build()
//    else this

//object AuthorizationInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response =
//        chain.proceed(chain.request().authorized)
//}