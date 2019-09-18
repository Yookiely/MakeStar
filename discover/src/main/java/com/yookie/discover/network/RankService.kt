package com.yookie.discover.network

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface RankService {
    @GET("userRank/getMonthRank")
    fun getMonthUserRank(@Query("num") num : Int) : Deferred<CommonBody<List<userData>>>


    @GET("workRank/getMonthRank")
    fun getMonthWorkRank(@Query("num") num : Int) : Deferred<CommonBody<List<workData>>>

    @GET("aliyun/getVideoAuth")
    fun getProof(@Query("videoID") video_ID: String) : Deferred<CommonBody<proofData>>

    @GET("activity/getRecentActivitys")
    fun getActivity(@Query("page")page : Int,@Query("limit") limit : String) : Deferred<CommonBody<activityData>>


    companion object : RankService by ServiceFactory()

}

data class workData(
    val work_ID: Int,
    val work_name: String,
    val username: String,
    val month_hot_value: String,
    val cover_url: String,
    val video_ID: String,
    val tag : List<String>
)



data class userData(
    val avatar: String,
    val month_hot_value: Int,
    val user_ID: Int,
    val username: String
)


data class proofData(
    val RequestId: String,
    val VideoMeta: VideoMeta,
    val PlayAuth: String
)

data class VideoMeta(
    val CoverURL: String,
    val Status: String,
    val VideoId: String,
    val Duration: Double,
    val Title: String
)

data class activityData(
    val data: List<Data>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: String,
    val prev_page_url: String,
    val to: Int,
    val total: Int
)

data class Data(
    val activity_ID: Int,
    val activity_name: String,
    val address: String,
    val cover_url: String,
    val finish_time: String,
    val introduction: String,
    val progress: String,
    val start_time: String,
    val the_star_ID: Int,
    val the_star_avatar: String,
    val the_star_username: String,
    val update_time: String
)
