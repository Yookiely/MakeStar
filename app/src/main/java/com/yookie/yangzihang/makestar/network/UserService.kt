package com.yookie.yangzihang.makestar.network

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService{
    @GET("user/collection")
    fun getcollection() :  Deferred<CommonBody<collections>>

    @GET("user/getHistoryRank")
    fun getHistoryRank() : Deferred<CommonBody<List<rank>>>

    @POST("message/getMessageByUserID")
    fun getMessage(@Query("user_ID") userid : String ,@Query("model") model : String,@Query("limit") limit : String,@Query("page")page : Int) : Deferred<CommonBody<message>>

    @POST("message/getMessageByID")
    fun getMessageById(@Query("message_ID")message_ID: String) : Deferred<CommonBody<List<messageData>>>

    @POST("message/setMessageRead")
    fun setMessageRead(@Query("message_ID")message_ID: String) : Deferred<result>

    companion object : UserService by ServiceFactory()
}


data class collections(
    val `0`: X0,
    val currentPage: Int,
    val lastPage: Int
)

data class X0(
    val collection_ID: Int,
    val collection_num: Int,
    val comment_num: Int,
    val cover_ID: String,
    val cover_url: String,
    val hot_value: Int,
    val introduction: Any,
    val share_num: Int,
    val tag: String,
    val time: String,
    val video_ID: String,
    val video_link: String,
    val work_name: String,
    val work_type: String
)
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


