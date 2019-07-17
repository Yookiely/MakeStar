package com.example.common

import com.example.common.experimental.network.CommonBody
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthService{
   @GET("/api/{user}/{register}")
   fun getToken(@Path("user") user : String,@Path("register") register : String) : Deferred<CommonBody<AuthData>>
}





data class AuthData(
    val user_ID: String,
    val username: String,
    val avatar: String,
    val sex: String,
    val age: Int,
    val signature: String,
    val fans_num: String,
    val token: String,
    val week_hot_value: String,
    val month_hot_value: String,
    val year_hot_value: String,
    val spotted: Int,
    val city: String
)