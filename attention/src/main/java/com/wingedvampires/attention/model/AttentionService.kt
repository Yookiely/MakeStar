package com.wingedvampires.attention.model

import com.example.common.experimental.network.CommonBody
import com.example.common.experimental.network.ServiceFactory
import com.example.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface AttentionService {

    @GET("/api/user/getFollowUserVideoAction")
    fun getFollowUserVideoAction(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 5,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<VideoAction>>>

    @GET("/api/user/spotList")
    fun getSpotList(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int,
        @Query("uer_ID") userId: String
    ): Deferred<CommonBody<List<ConcernPerson>>>

    @GET("/api/follow/add")
    fun addFollow(
        @Query("user_ID") userId: Int,
        @Query("username") userName: String
    ): Deferred<CommonBody<String>>


    @GET("/api/follow/delete")
    fun deleteFollow(
        @Query("user_ID") userId: Int,
        @Query("username") userName: String
    ): Deferred<CommonBody<String>>

    companion object : AttentionService by ServiceFactory()
}

data class VideoAction(
    val avatar: String,
    val cover_ID: String,
    val cover_url: String,
    val time: String,
    val user_ID: String,
    val username: String,
    val video_ID: String,
    val work_ID: String,
    val work_name: String,
    val Duration: Float
)

data class ConcernPerson(
    val age: Int,
    val avatar: String,
    val city: String,
    val fans_num: Int,
    val month_hot_value: Int,
    val sex: String,
    val signature: String,
    val spotted: Int,
    val username: String,
    val week_hot_value: Int,
    val year_hot_value: Int
)