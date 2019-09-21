package com.yookie.common

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("user/register")
    fun register(@Query("username") user: String, @Query("password") register: String): Deferred<CommonBody<NewUser>>

    @POST("user/login")
    fun getToken(@Query("username") user: String, @Query("password") register: String): Deferred<CommonBody<AuthData>>


    @GET("user/myself")
    fun authSelf(): Deferred<CommonBody<AuthData>>


    companion object : AuthService by ServiceFactory()
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


data class NewUser(
    val token: String
)
