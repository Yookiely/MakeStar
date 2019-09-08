package com.example.common.experimental.network

import okhttp3.Interceptor
import okhttp3.Request

internal inline val Request.isTrusted
    get() = url().host() in trustedHosts

private val trustedHosts = setOf("api.buildstar.top:8888")
/**
 * A wrapped interceptor only applied to trusted request.
 *
 * @see ServiceFactory
 */
internal val Interceptor.forTrusted
    get() = Interceptor {
        if (it.request().isTrusted) intercept(it)
        else it.proceed(it.request())
    }