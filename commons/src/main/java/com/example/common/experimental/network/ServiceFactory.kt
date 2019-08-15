package com.example.common.experimental.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceFactory {

    internal const val TRUSTED_HOST = "api.buildstar.top:8888"
    internal const val BASE_URL = "https://$TRUSTED_HOST/api/"

    internal const val APP_KEY = "af4dg52ed%3@"
    internal const val APP_SECRET = "1aVhfAYBFUfqrdlcT621d9d6OzahMI"

    private val loggingInterceptor = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder()
//        .addInterceptor(UserAgentInterceptor.forTrusted)
        .addInterceptor(SignatureInterceptor.forTrusted)
//        .addInterceptor(AuthorizationInterceptor.forTrusted)
//        .authenticator(RealAuthenticator)
        .retryOnConnectionFailure(false)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addNetworkInterceptor(loggingInterceptor)
//        .addNetworkInterceptor(CodeCorrectionInterceptor.forTrusted)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    inline operator fun <reified T> invoke(): T = retrofit.create(T::class.java)
}






























data class CommonBody<out T>(
    val error_code: Int,
    val message: String,
    val data: T?
)