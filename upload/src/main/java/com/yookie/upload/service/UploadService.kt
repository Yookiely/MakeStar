package com.yookie.upload.service

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.POST
import retrofit2.http.Query

interface UploadService {
    @POST("aliyun/getVideoUpload")
    fun getVideoUpload(@Query("title") title:String,@Query("fileName") fileName : String,@Query("description") description: String,@Query("coverID")coverID:String,@Query("tags")tags:String) : Deferred<CommonBody<videoUpload>>

    @POST("aliyun/getCoverUpload")
    fun getCoverUpload(@Query("ext") ext : String) : Deferred<CommonBody<coverUpload>>

    companion object  : UploadService by ServiceFactory()
}

data class videoUpload(
    val RequestId: String,
    val UploadAddress: String,
    val UploadAuth: String,
    val VideoId: String
)

data class coverUpload(
    val FileURL: String,
    val ImageId: String,
    val ImageURL: String,
    val RequestId: String,
    val UploadAddress: String,
    val UploadAuth: String
)