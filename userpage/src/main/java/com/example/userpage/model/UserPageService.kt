package com.example.userpage.model

import com.example.common.experimental.network.CommonBody
import kotlinx.coroutines.experimental.Deferred
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserPageService{
    @GET("/api/user/myself")
    fun getMyInfo(@Query("user_ID") userID : String) : Deferred<CommonBody<MyUserData>>

    @POST("/api/user/update")
    fun updateInfo(@Part partList: List<MultipartBody.Part>) : Deferred<CommonBody<String>>

    @GET("/api/user/logout")
    fun logout(@Query("user_ID") userID: String) : Deferred<CommonBody<String>>
}


data class MyUserData(
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