package com.yookie.common.experimental.network

import okhttp3.*
import java.util.*

internal typealias ParamsMap = Map<String, String>


internal inline fun <T> usingTimeStamp(crossinline block: (String) -> T) =
    Calendar.getInstance().timeInMillis.toString().let(block)

internal inline val ParamsMap.timeStampAndSignature: Pair<String, String>
    get() = usingTimeStamp { t ->
        toSortedMap().apply {
            // put timestamp into params map
            put("t", t)
        }.asSequence().joinToString( // join params in dict order
            separator = "",
            prefix = ServiceFactory.APP_KEY,
            postfix = ServiceFactory.APP_SECRET
        ) { (name, value) -> name + value }.let {
            t to kotlin.text.String(
                org.apache.commons.codec.binary.Hex.encodeHex(
                    org.apache.commons.codec.digest.DigestUtils.sha1(
                        it
                    )
                )
            ).toUpperCase()
        }
    }

internal inline val HttpUrl.queryMap: ParamsMap
    get() = (0 until querySize()).associate {
        queryParameterName(it) to queryParameterValue(it)
    }

internal inline val FormBody.fieldMap: ParamsMap
    get() = (0 until size()).associate {
        name(it) to value(it)
    }
internal inline val FormBody.newBuilder
    get() = (0 until size()).associateTo(mutableMapOf()) {
        encodedName(it) to encodedValue(it)
    }.asSequence().fold(FormBody.Builder()) { builder, (encodedName, encodedValue) ->
        builder.addEncoded(encodedName, encodedValue)
    }

internal inline val HttpUrl.signed
    get() = queryMap.timeStampAndSignature.let { (t, sign) ->
        newBuilder().addQueryParameter("t", t)
            .addQueryParameter("sign", sign)
            .addQueryParameter("app_key", ServiceFactory.APP_KEY)
            .build()
    }

internal inline val RequestBody.signed
    get() = when (this) {
        is FormBody -> fieldMap.timeStampAndSignature.let { (t, sign) ->
            newBuilder.add("t", t)
                .add("sign", sign)
                .add("app_key", ServiceFactory.APP_KEY)
                .build()
        }
        else -> this
    }

internal inline val Request.signed
    get() = when (method()) {
        "HEAD" -> this
        "GET", "DELETE" -> newBuilder().url(url().signed).build()
        "POST", "PUT", "PATCH" -> newBuilder().method(method(), body()?.signed).build()
        else -> this
    }

internal object SignatureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().signed)
}