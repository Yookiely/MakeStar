package com.wingedvampires.homepage.model

import com.example.common.experimental.network.CommonBody
import com.example.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface HomePageService {

    @GET("/api/banner/getRecentBannerByType")
    fun getRecentBannerByType(
        @Query("limit") limit: Int = 10,
        @Query("bannerType") bannerType: Int = 1
    ): Deferred<CommonBody<List<Banner>>>

    @GET("/api/work/getWorkTypes")
    fun getWorkTypes(): Deferred<CommonBody<List<WorkType>>>

    @GET("/api/work/getRecommendWork")
    fun getRecommendWork(
        @Query("history") history: String = HomePageUtils.setAndGetUserHistory(),
        @Query("habit") habit: String = HomePageUtils.setAndGetUserHabit(),
        @Query("limit") limit: Int = 10,
        @Query("mode") mode: Int = 1
    ): Deferred<CommonBody<List<Work>>>

    @GET("/api/work/getWorkByTypeID")
    fun getWorkByTypeID(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int,
        @Query("work_type_ID") workTypeId: Int
    ): Deferred<CommonBody<WorksWithType>>

    @GET("/api/work/getWorkByUserID")
    fun getWorkByUserID(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int,
        @Query("user_ID") userId: Int
    ): Deferred<CommonBody<WorksWithType>>


    companion object : HomePageService by ServiceFactory()
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
    val total: Int,
    val tag: String
)

data class Work(
    val Introduction: String,
    val avatar: String,
    val cover_url: String,
    val hot_value: Int,
    val tags: String,
    val time: String,
    val user_ID: Int,
    val username: String,
    val video_ID: String,
    val video_link: Any,
    val work_ID: Int,
    val work_name: String,
    val work_type_ID: Int
)

data class Banner(
    val banner_ID: Int,
    val banner_img_ID: String,
    val banner_type: Int,
    val banner_url: String,
    val time: String
)

data class WorkType(
    val work_type_ID: Int,
    val work_type_name: String
)