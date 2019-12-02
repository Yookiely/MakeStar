package com.wingedvampires.homepage.model

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HomePageService {

    @GET("/api/banner/getRecentBannerByType")
    fun getRecentBannerByType(
        @Query("limit") limit: Int = 4,
        @Query("bannerType") bannerType: Int = 1,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<Banner>>>

    @GET("/api/work/getWorkTypes")
    fun getWorkTypes(): Deferred<CommonBody<List<WorkType>>>

    @GET("/api/work/getRecommendWork")
    fun getRecommendWork(
        @Query("history") history: String? = CommonPreferences.setAndGetUserHistory(),
        @Query("habit") habit: String? = CommonPreferences.setAndGetUserHabit(),
        @Query("limit") limit: Int = 10,
        @Query("mode") mode: Int = 1,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<Work>>>

    @GET("/api/work/getWorkByTypeID")
    fun getWorkByTypeID(
        @Query("page") page: Int,
        @Query("work_type_ID") workTypeId: Int,
        @Query("limit") limit: Int = 10,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<WorksWithType>>

    @GET("/api/work/getWorkByUserID")
    fun getWorkByUserID(
        @Query("page") page: Int,
        @Query("user_ID") userId: String,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<WorksWithType>>

    @POST("/api/search")
    fun search(
        @Query("page") page: Int,
        @Query("content") content: String,
        @Query("user_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<SearchData>>

    @GET("task/getWeekInfo")
    fun getRedPacketInfo(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<RedPacketInfo>>


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
    val total: Int
)

data class Work(
    val Duration: Float,
    val Introduction: String,
    val avatar: String,
    val collection_num: Int,
    val comment_num: Int,
    val cover_url: String,
    val hot_value: Int,
    val tags: String,
    val time: String,
    val user_ID: String,
    val username: String,
    val video_ID: String,
    val video_link: Any,
    val work_ID: String,
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

data class NewStar(
    val numberOfStar: String
)

data class SearchData(
    val user: DataAndPage<DataOfUser>,
    val work: DataAndPage<DataOfWork>
)

data class DataOfUser(
    val age: String,
    val avatar: String,
    val city: String?,
    val sex: String,
    val signature: String?,
    val username: String,
    val user_ID: String,
    val tags: String?
)

data class DataOfWork(
    val collection_num: String,
    val comment_num: String,
    val cover_ID: String,
    val cover_url: String?,
    val hot_value: String,
    val introduction: String,
    val share_num: String,
    val tags: String,
    val time: String,
    val username: String,
    val video_ID: String,
    val video_link: String?,
    val work_ID: Int,
    val work_name: String,
    val work_type: String
)


data class DataAndPage<T>(
    val data: List<T>?,
    val currentPage: Int,
    val lastPage: Int
)

data class RedPacketInfo(
    val day_1: String,
    val day_2: String,
    val day_3: String,
    val day_4: String,
    val day_5: String,
    val day_6: String,
    val day_7: String,
    val is_today_signed: Boolean,
    val is_today_take_upload_money: Boolean,
    val is_today_take_watch_money: Boolean,
    val Invitation_code: String,
    val Invitation_count: Int,
    val red_bag_count: Int,
    val today_total_upload: Int,
    val today_total_watch: Int,
    val total_money: String,
    val user_ID: Int
)