package com.wingedvampires.homepage.model

import com.example.common.experimental.network.CommonBody
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface HomePageService {

    @GET("/api/work/getWorkByTypeID")
    fun getWorkByTypeID(@Query("limit") limit: Int = 10, @Query("page") page: Int, @Query("work_type_ID") workTypeId: Int): Deferred<CommonBody<WorksWithType>>

    @GET("/api/work/getWorkByUserID")
    fun getWorkByUserID(@Query("limit") limit: Int = 10, @Query("page") page: Int, @Query("user_ID") userId: Int): Deferred<CommonBody<WorksWithType>>
}

data class WorksWithType(
    val current_page: Int,
    val data: List<Work>,
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

data class Work(
    val Introduction: String,
    val avatar: Any,
    val cover_url: String,
    val hot_value: Int,
    val time: String,
    val user_ID: Int,
    val username: String,
    val video_ID: String,
    val video_link: String,
    val work_ID: Int,
    val work_name: String,
    val work_type_ID: Int
)

