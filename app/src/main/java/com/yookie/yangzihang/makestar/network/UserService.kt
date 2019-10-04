package com.yookie.yangzihang.makestar.network

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService{

    @GET("user/getHistoryRank")
    fun getHistoryRank(@Query("user_ID") userid: String) : Deferred<CommonBody<List<rank>>>

    @POST("message/getMessageByUserID")
    fun getMessage(@Query("user_ID") userid : String ,@Query("model") model : String,@Query("limit") limit : String,@Query("page")page : Int) : Deferred<CommonBody<message>>

    @POST("message/getMessageByID")
    fun getMessageById(@Query("message_ID")message_ID: String) : Deferred<CommonBody<List<messageData>>>

    @POST("message/setMessageRead")
    fun setMessageRead(@Query("message_ID")message_ID: String) : Deferred<result>

    @GET("user/collection")
    fun getCollection(@Query("limit") limit : Int ,@Query("page") page: Int,@Query("user_ID")userid: String) : Deferred<CommonBody<collection>>

    @GET("task/getWeekInfo")
    fun getRedPacketInfo(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<RedPacketInfo>>

    @GET("task/signDay")
    fun signDay(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<SignDay>>

    @GET("task/getWatchMoney")
    fun getWatchMoney(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<WatchMoney>>


    @GET("task/getUploadMoney")
    fun getUploadMoney(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<UploadMoney>>


    companion object : UserService by ServiceFactory()

}


data class rank(
    val month: String,
    val rank: String,
    val year: String
)

data class message(
    val data: List<messageData>,
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

data class messageData(
    val avatar: String,
    val content: String,
    val from: String,
    val message_ID: Int,
    val time: String,
    val username: String
)

data class result(
    val error_code: Int,
    val message: String
)

data class collection(
    val data: List<collectionData>,
    val currentPage: Int,
    val lastPage: Int
)

data class collectionData(
    val collection_ID: Int,
    val collection_num: Int,
    val comment_num: Int,
    val cover_ID: String,
    val cover_url: String,
    val hot_value: Int,
    val introduction: String,
    val share_num: Int,
    val tag: String,
    val time: String,
    val video_ID: String,
    val video_link: String,
    val work_name: String,
    val work_type: String
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
    val red_bag_count: Int,
    val today_total_upload: Int,
    val today_total_watch: Int,
    val total_money: String,
    val user_ID: Int
)

data class SignDay(
    val current_time_in_week: Int,
    val this_time_get_money: String,
    val time: Int,
    val user_ID: String,
    val week: Int
)

data class WatchMoney(
    val day_watch_count: Int,
    val this_time_get_money: String,
    val user_ID: String,
    val y_m_d: String
)

data class UploadMoney(
    val day_upload_count: Int,
    val this_time_get_money: String,
    val user_ID: String,
    val y_m_d: String
)