package com.yookie.common.experimental.network

import com.orhanobut.logger.Logger
import com.yookie.common.AuthService
import com.yookie.common.experimental.CommonContext
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import java.io.IOException
import java.net.HttpURLConnection

internal inline val Request.authorized
    get() = if (header("Authorization") == null)
        newBuilder().addHeader("Authorization", "Bearer ${CommonPreferences.token}").build()
    else this

object AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().authorized)
}

object RealAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val responseBodyCopy = response.peekBody(Long.MAX_VALUE) // 避免responseBody被一次性清空
        val request = if (response.request().isTrusted) {
//            val code = JSONObject(responseBodyCopy.string()).getInt("error_code")//后端没办法401里面带error_code
            val relogin = fun(): Nothing {
                launch(UI) {
                    if (CommonPreferences.isWechat) {
                        AuthService.loginForWechat(CommonPreferences.wechat_openid)
                            .awaitAndHandle {
                                CommonContext.startActivity(name = "login")
                            }?.data?.token?.let { CommonPreferences.token = it }
                    } else if (CommonPreferences.isQQ) {
                        AuthService.qqLogin(CommonPreferences.qq_openid)
                            .awaitAndHandle {
                                CommonContext.startActivity(name = "login")
                            }?.data?.token?.let { CommonPreferences.token = it }
                    } else {
                        AuthService.getToken(CommonPreferences.username, CommonPreferences.password)
                            .awaitAndHandle {
                                CommonContext.startActivity(name = "login")
                            }?.data?.token?.let { CommonPreferences.token = it }
                    }
                }
                throw IOException("登录失效，正在尝试自动重登")
            }
            val code = 10003
            when (code) {
                10001 ->
                    if (response.priorResponse()?.request()?.header("Authorization") == null)
                        CommonPreferences.token
                    else relogin()
                10003, 10004 -> relogin()

//                30001, 30002 -> {
//                    val loggingIn = CommonContext.getActivity("login")
//                        ?.isInstance(Restarter.getForegroundActivity(null))
//                        ?: false
//                    if (!loggingIn) {
//                        CommonPreferences.isLogin = false
//                        CommonContext.startActivity(name = "login")
//                    }
//                    throw IOException("帐号或密码错误")
//                }
                else -> {
                    Logger.d(
                        """
                                        Unhandled error code $code, for
                                        Request: ${response.request()}
                                        Response: $response
                                        """.trimIndent()
                    )
                    return null // 交给外界处理 不要走Authenticator
                }
            }.let {
                response.request().newBuilder()
                    .header("Authorization", "Bearer{$it}").build()
            }
        } else null
        return request
    }
}

object CodeCorrectionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request()).run {
            if (code() == HttpURLConnection.HTTP_BAD_REQUEST)
                newBuilder().code(HttpURLConnection.HTTP_UNAUTHORIZED).build() else this
        }
}