package com.yookie.common

import com.yookie.common.experimental.CommonContext
import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.CoroutineCallAdapterFactory
import com.yookie.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AuthService {
    @POST("user/register")
    fun register(@Query("username") user: String, @Query("password") register: String): Deferred<CommonBody<NewUser>>

    @POST("user/login")
    fun getToken(@Query("username") user: String, @Query("password") register: String): Deferred<CommonBody<AuthData>>


    @GET("user/myself")
    fun authSelf(): Deferred<CommonBody<AuthData>>


    companion object : AuthService by ServiceFactory()
}

interface WeiXinService {

    @GET("oauth2/access_token")
    fun getAccessToken(
        @Query("code") code: String,
        @Query("appid") appid: String = CommonContext.WECHAT_APPID,
        @Query("secret") secret: String = CommonContext.WECHAT_SECRET,
        @Query("grant_type") grant_type: String = "authorization_code"
    ): Deferred<WeiXinToken>

    @GET("userinfo")
    fun getUserInfo(
        @Query("access_token") access_token: String,
        @Query("openid") openid: String
    ): Deferred<WeiXinInfo>

    companion object : WeiXinService by WeiXinServiceFactory()
}

object WeiXinServiceFactory {

    internal const val BASE_URL = "https://api.weixin.qq.com/sns/"

    private val loggingInterceptor = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(40, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .addNetworkInterceptor(loggingInterceptor)
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

data class AuthData(
    val age: Int,
    val avatar: String,
    val city: String,
    val fans_num: Int,
    val follow_num: Int,
    val month_hot_value: Int,
    val month_rank: Int,
    val sex: String,
    val signature: String,
    val tags: String,
    val token: String,
    val user_ID: Int,
    val username: String,
    val week_hot_value: Int,
    val year_hot_value: Int
)

data class WeiXin(
    var type: Int,//1:登录 2.分享 3:微信支付
    var errCode: Int,//微信返回的错误码
    var code: String?//登录成功才会有的code
)

data class WeiXinInfo(
    var openid: String?,//
    var headimgurl: String?,//用户头像URL
    var nickname: String?,
    var age: Int
)

data class WeiXinPay(
    var noncestr: String? = "",//随机字符串，不长于32位。推荐随机数生成算法
    var package_value: String? = "",//暂填写固定值Sign=WXPay
    var partnerid: String? = "",//微信支付分配的商户号
    var prepayid: String? = "",//微信返回的支付交易会话ID
    var timestamp: String? = "",//时间戳
    var sign: String? = ""//签名
)

data class WeiXinToken(
    var access_token: String?,// 接口调用凭证
    var expires_in: String?,// access_token接口调用凭证超时时间，单位（秒）
    var refresh_token: String?,// 用户刷新access_token
    var openid: String?,// 授权用户唯一标识
    var scope: String?,// 用户授权的作用域，使用逗号（,）分隔
    var unionid: String?// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
)

data class NewUser(
    val token: String
)
