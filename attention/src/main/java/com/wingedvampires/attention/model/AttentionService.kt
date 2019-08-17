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

    @GET("/api/user/getRecommendUser")
    fun getRecommendUser(
        @Query("page") page: Int,
        @Query("uer_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<List<RecommendUser>>>

    @GET("/api/user/getFuns")
    fun getFans(
        @Query("page") page: Int,
        @Query("uer_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<List<Fan>>>

    @GET("/api/user/spotList")
    fun getSpotList(
        @Query("page") page: Int,
        @Query("uer_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<List<ConcernPerson>>>

    @GET("/api/search")
    fun search(
        @Query("page") page: Int,
        @Query("content") content: String,
        @Query("uer_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<SearchData>>

    @GET("/api/follow/add")
    fun addFollow(
        @Query("to_user_ID") toUserId: String,
        @Query("from_user_ID") fromUserId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>


    @GET("/api/follow/delete")
    fun deleteFollow(
        @Query("to_user_ID") toUserId: String,
        @Query("from_user_ID") fromUserId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    companion object : AttentionService by ServiceFactory()
}

data class VideoAction(
    val Duration: Float,
    val share_num: String,
    val hot_value: String,
    val avatar: String,
    val collection_num: String,
    val comment_num: String,
    val cover_ID: String,
    val cover_url: String,
    val month_rank: Int,
    val signature: String?,
    val tags: String,
    val time: String,
    val user_ID: Int,
    val username: String,
    val video_ID: String,
    val work_ID: Int,
    val work_name: String
)

data class RecommendUser(
    val avatar: String,
    val fans_num: Int,
    val user_ID: String,
    val username: String,
    val signature: String?,
    val tags: String,
    val month_rank: Int
)

data class Fan(
    val avatar: String,
    val user_ID: Int,
    val username: String,
    val month_rank: Int,
    val signature: String?
)

data class ConcernPerson(
    val user_ID: String,
    val age: Int,
    val avatar: String,
    val city: String,
    val fans_num: String,
    val follow_num: String,
    val month_hot_value: Int,
    val sex: String,
    val month_rank: Int,
    val signature: String?,
    val username: String,
    val week_hot_value: Int,
    val year_hot_value: Int,
    val tags: String
)

data class SearchData(
    val user: SearchUser,
    val work: SearchWork
)

data class SearchUser(
    val data: List<DataOfUser>,
    val currentPage: Int,
    val lastPage: Int
)

data class DataOfUser(
    val age: String,
    val avatar: String,
    val city: String,
    val sex: String,
    val signature: String?,
    val username: String,
    val user_ID: String
)

data class SearchWork(
    val data: List<DataOfWork>,
    val currentPage: Int,
    val lastPage: Int
)

data class DataOfWork(
    val hot_value: String,
    val introduction: String,
    val tags: String,
    val time: String,
    val username: String,
    val video_link: String,
    val work_name: String,
    val work_type: Int,
    val work_ID: String
)