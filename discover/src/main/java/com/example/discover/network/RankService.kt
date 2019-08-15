package com.example.discover.network

import com.example.common.experimental.network.CommonBody
import com.example.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface RankService {
    @GET("/api/userRank/getMonthRank")
    fun getMonthUserRank(@Query("num") num : Int) : Deferred<CommonBody<List<userData>>>


    @GET("/api/workRank/getMonthRank")
    fun getMonthWorkRank(@Query("num") num : Int) : Deferred<CommonBody<List<workData>>>

    @GET("/api/aliyun/getVideoAuth")
    fun getProof(@Query("videoID") video_ID: String) : Deferred<CommonBody<proofData>>


    companion object : RankService by ServiceFactory()

}

data class workData(
    val work_ID: Int,
    val work_name: String,
    val username: String,
    val week_hot_value: String,
    val cover_url: String,
    val video_ID: String,
    val tag : List<String>
)



data class userData(
    val user_ID: Int,
    val username: String,
    val avatar: String,
    val week_hot_value: Int
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
