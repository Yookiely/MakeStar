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
    fun authSelf(@Query("user_ID")user_ID: String): Deferred<CommonBody<AuthData>>


    companion object : AuthService by ServiceFactory()
}


//data class AuthData(
//    val user_ID: String,
//    val username: String,
//    val avatar: String,
//    val sex: String,
//    val age: Int,
//    val signature: String,
//    val fans_num: String,
//    val token: String,
//    val week_hot_value: String,
//    val month_hot_value: String,
//    val year_hot_value: String,
//    val spotted: String,
//    val city: String
//)

data class AuthData(
    val age: String,
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
    val user_ID: String,
    val username: String,
    val week_hot_value: Int,
    val year_hot_value: Int
)


data class NewUser(
    val token: String
)
