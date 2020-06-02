package com.yookie.upload.service

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UploadService {
    @POST("aliyun/getVideoUpload")
    fun getVideoUpload(@Query("title") title:String,@Query("fileName") fileName : String,@Query("description") description: String,@Query("coverID")coverID:String,@Query("tags")tags:String,@Query("workType") workType : Int,@Query("userID") userId : String) : Deferred<CommonBody<videoUpload>>

    @POST("aliyun/getCoverUpload")
    fun getCoverUpload(@Query("ext") ext : String) : Deferred<CommonBody<coverUpload>>

    @POST("action/sendNewAction")
    fun sendAction(@Query("user_ID") userId : String , @Query("content") content : String,@Query("img_IDs") imgIDs : String,@Query("be_at_user_ID") atuser : String) : Deferred<actionResponse>
    @POST("fandomAction/sendNewAction")
    fun sendNewAction(@Query("user_ID") userId : String, @Query("host_user_ID") hostId:String, @Query("content") content : String, @Query("img_IDs") imgIDs : String, @Query("be_at_user_ID") atuser : String) : Deferred<actionResponse>
    @GET("Ack/workUploadAck")
    fun workUploadAck(@Query("work_ID") workId : String , @Query("user_ID") userid : String = CommonPreferences.userid) : Deferred<uploadResponse>

    @GET("task/uploadOneTime")
    fun uploadOneTime(
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<UploadResult>>

    companion object  : UploadService by ServiceFactory()
}

data class UploadResult(
    val day_upload_count: Int,
    val this_time_get_money: Int,
    val user_ID: String,
    val y_m_d: String
)
data class videoUpload(
    val RequestId: String,
    val UploadAddress: String,
    val UploadAuth: String,
    val VideoId: String,
    val work_ID : String
)

data class coverUpload(
    val FileURL: String,
    val ImageId: String,
    val ImageURL: String,
    val RequestId: String,
    val UploadAddress: String,
    val UploadAuth: String
)

data class actionResponse(
    val error_code: Int,
    val message: String
)


data class uploadResponse(
    val error_code: Int,
    val message: String,
    val work_ID : String
)