package com.wingedvampires.attention.model

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VideoPlayService {

    @GET("/api/work/getWorkByID")
    fun getWorkByID(
        @Query("work_ID") workId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<WorkById>>>

    @GET("/api/aliyun/getVideoInfo")
    fun getVideoInfo(
        @Query("videoID") workId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<VideoInfo>>

    @GET("/work/addCollection")
    fun addCollection(
        @Query("work_ID") workId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String?>>

    @GET("/work/deleteCollectionByWork")
    fun deleteCollection(
        @Query("work_ID") workId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String?>>

    @POST("/api/follow/add")
    fun addFollow(
        @Query("to_user_ID") toUserId: String,
        @Query("from_user_ID") fromUserId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>


    @POST("/api/follow/delete")
    fun deleteFollow(
        @Query("to_user_ID") toUserId: String,
        @Query("from_user_ID") fromUserId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    companion object : VideoPlayService by ServiceFactory()
}

data class WorkById(
    val Duration: String,
    val Introduction: String,
    val avatar: String,
    val collection_num: String,
    val comment_num: String,
    val cover_url: String,
    val hot_value: String,
    val is_collected: Boolean,
    val share_num: String,
    val tags: String,
    val time: String,
    val user_ID: String,
    val username: String,
    val video_ID: String,
    val video_link: String,
    val work_ID: String,
    val work_name: String,
    val work_type_ID: Int
)

data class VideoInfo(
    val PlayInfoList: PlayInfo,
    val RequestId: String,
    val VideoBase: VideoBase
)

data class PlayInfo(
    val PlayInfo: List<PlayInfoDetail>
)

data class PlayInfoDetail(
    val Bitrate: String,
    val CreationTime: String,
    val Definition: String,
    val Duration: String,
    val Encrypt: Int,
    val Format: String,
    val Fps: String,
    val Height: Int,
    val JobId: String,
    val ModificationTime: String,
    val NarrowBandType: String,
    val PlayURL: String,
    val PreprocessStatus: String,
    val Size: Int,
    val Specification: String,
    val Status: String,
    val StreamType: String,
    val Width: Int
)

data class VideoBase(
    val CoverURL: String,
    val CreationTime: String,
    val Duration: String,
    val MediaType: String,
    val OutputType: String,
    val Status: String,
    val Title: String,
    val TranscodeMode: String,
    val VideoId: String
)