package com.example.yangzihang.makestar.network

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface UserService {
    @GET("user/getHistoryRank")
    fun getHistoryRank(@Query("user_ID") userid: String): Deferred<CommonBody<List<rank>>>

    @POST("message/getMessageByUserID")
    fun getMessage(
        @Query("user_ID") userid: String, @Query("model") model: String, @Query("limit") limit: String, @Query(
            "page"
        ) page: Int
    ): Deferred<CommonBody<message>>

    @POST("message/getMessageByID")
    fun getMessageById(@Query("message_ID") message_ID: String): Deferred<CommonBody<List<messageData>>>

    @POST("message/setMessageRead")
    fun setMessageRead(@Query("message_ID") message_ID: String): Deferred<result>

    @GET("user/collection")
    fun getCollection(@Query("limit") limit: Int, @Query("page") page: Int, @Query("user_ID") userid: String): Deferred<CommonBody<collection>>

    @GET("task/getWeekInfo")
    fun getRedPacketInfo(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<RedPacketInfo>>

    @GET("task/signDay")
    fun signDay(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<SignDay>>

    @GET("task/getWatchMoney")
    fun getWatchMoney(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<WatchMoney>>


    @GET("task/getUploadMoney")
    fun getUploadMoney(@Query("user_ID") userid: String = CommonPreferences.userid): Deferred<CommonBody<UploadMoney>>

    @GET("task/useInvitationCode")
    fun useInvitationCode(
        @Query("code") code: String,
        @Query("user_ID") userid: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<Any>>>

    @GET("work/getWorkByUserID")
    fun getmyVideoList(@Query("user_ID")userid: String,@Query("page") page: Int,@Query("limit")limit : Int) : Deferred<CommonBody<MyVideoList>>

    @GET("user/userinfo")
    fun getUserInfo(@Query("user_ID")userid: String) : Deferred<CommonBody<UserInfo>>

    @GET("fandomAction/getFandomInfo")
    fun getFandomInfo(@Query("host_user_ID") hostUserID :Int  ,@Query("user_ID") userid: String = CommonPreferences.userid ):Deferred<CommonBody<FandomInfo>>

    @FormUrlEncoded
    @POST("fandomAction/changeStatusOfFandom")
    fun changeStatusOfFandom(@FieldMap params : Map<String,String>): Deferred<CommonBody<StatusOfFandom>>

    @GET("fandomAction/getUserFandomList")
    fun getUserFandomList(@Query("user_ID") userid: String = CommonPreferences.userid) :Deferred<CommonBody<FandomListData>>

    @GET("fandomAction/getRecentActions")
    fun getRecentActions(@Query("limit") limit: Int, @Query("page") page: Int, @Query("host_user_ID") hostUserId: Int ):Deferred<CommonBody<FansCircleInfo>>

    @GET("user/getFollowUserAction")
    fun getUserActions(@Query("limit") limit: Int, @Query("page") page: Int, @Query("user_ID") hostUserId: Int ):Deferred<CommonBody<UserActive>>

    @GET("fandomAction/getCommentByActionID")
    fun getCommemtByActionID(@Query("fandom_action_ID") fandom_action_ID: Int,@Query("limit") limit: Int,@Query("page")page: Int):Deferred<CommonBody<FansComment>>

    @GET("fandomAction/getCommentCommentByAcID")
    fun getCommentCommentByAcID(@Query("facID") facID: Int,@Query("limit") limit: Int ,@Query("page")page: Int) : Deferred<CommonBody<FansSeComment>>

    @GET("tag/getConstUserTags")
    fun getConstUserTags(): Deferred<CommonBody<List<ConstUserTag>>>

    @GET("systemMessage/getUserNewMessageNum")
    fun getNewMessageCount(@Query("userID") userid: String) : Deferred<CommonBody<NewMessage>>

    @GET("systemMessage/getAllSystemMessageList")
    fun getAllMessage(@Query("page")page: Int,@Query("limit")limit: Int,@Query("userID")userid: String) : Deferred<CommonBody<Comment>>

    @GET("systemMessage/getAllSystemStarList")
    fun getAllStar(@Query("page")page: Int,@Query("limit")limit: Int,@Query("userID")userid: String) : Deferred<CommonBody<StarMessage>>

    @GET("work/deleteCollection")
    fun deleteCollection(@Query("collection_ID") collection_ID: Int) : Deferred<CommonBody<String>>

    @GET("setting/getUserProtocol")
    fun getUserAgreement() : Deferred<CommonBody<infoData>>

    @GET("setting/getPrivacyProtocol")
    fun getPrivateAgreement() : Deferred<CommonBody<infoData>>

    @FormUrlEncoded
    @POST("message/sendMessage")
    fun sendMessage(@FieldMap params : Map<String,String>): Deferred<CommonBody<String>>
    companion object : UserService by ServiceFactory()


}

data class ConstUserTag(
    val tag_ID: Int,
    val tag_name: String
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

data class collection(
    val data: List<collectionData>,
    val currentPage: Int,
    val lastPage: Int
)

data class collectionData(
    val work_ID : Int,
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
    val Invitation_code: String,
    val Invitation_count: Int,
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

data class FandomInfo(
    val fandom_user_num: Int,
    val fans_num: Int,
    val host_username: String,
    val is_into: Boolean,
    val month_rank: Int,
    val total_fandom_action_num: Int,
    val total_hot: String,
    val total_work_num: Int
)
data class FandomListData(
    val my: List<MyCircle>,
    val others: List<OtherCircle>
)

data class OtherCircle(
    val host_avatar: String,
    val host_user_ID: Int,
    val host_username: String
)

data class MyCircle(
    val fans_num: Int,
    val host_avatar: String,
    val host_user_ID: Int,
    val host_username: String
)
data class StatusOfFandom(
    val error_code: Int,
    val message: String
)
data class FansCircleInfo(
    val `data`: List<FansCircleData>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: String,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class FansCircleData(
    val avatar: String,
    val content: String,
    val fandom_action_ID: Int,
    val img_IDs: String,
    val img_urls: List<String>,
    val time: String,
    val user_ID: Int,
    val username: String
)

data class FansComment(
    val `data`: List<FansCommentText>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: String,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class FansCommentText(
    val avatar: String,
    val be_at_user_ID: String,
    val facID: Int,
    val fac_content: String,
    val second_level_comment_num: Int,
    val time: String,
    val user_ID: Int,
    val username: String
)

data class FansSeComment(
    val `data`: List<FansSeCommentText>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: String,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class FansSeCommentText(
    val avatar: String,
    val be_at_user_ID: String,
    val faccID: Int,
    val facc_content: String,
    val time: String,
    val user_ID: Int,
    val username: String
)

data class MyVideo(
    val Introduction: String,
    val avatar: String,
    val cover_url: String,
    val hot_value: Int,
    val tags: String,
    val time: String,
    val user_ID: Int,
    val username: String,
    val video_ID: String,
    val work_ID: Int,
    val work_name: String,
    val work_type_ID: Int
)

data class MyVideoList(
    val data: List<MyVideo>,
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

data class UserInfo(
    val age: Int,
    val avatar: String,
    val city: String,
    val fans_num: Int,
    val follow_num: Int,
    val month_hot_value: Int,
    val month_rank: Int,
    val sex: String,
    val signature: String,
    val tags: String,
    val user_ID: Int,
    val username: String,
    val week_hot_value: Int,
    val year_hot_value: Int
)


data class NewMessage(
    val new_comment_num: Int,
    val new_message_num: Int,
    val new_star_num: Int,
    val new_user_message_num : Int
)
data class Comment(
    val data : List<MessageDatas>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: String,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class MessageDatas(
    val content: String,
    val direct_ID: Int,
    val direct_second_ID: Int,
    val direct_third_ID: Any,
    val from_user_ID: Int,
    val from_user_avatar: String,
    val from_user_name: String,
    val is_read: Int,
    val new_content: String,
    val old_content: String,
    val system_message_ID: Int,
    val time: String,
    val type: Int,
    val user_ID: Int
)


data class StarMessage(
    val data: List<StarData>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: String,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class StarData(
    val content: String,
    val from_user_ID: Int,
    val from_user_avatar: String,
    val from_user_name: String,
    val is_read: Int,
    val system_star_ID: Int,
    val time: String,
    val to_user_ID: Int,
    val work_ID: Int,
    val name : String,
    val introduction : String
)

data class infoData(
    val info_title : String,
    val info_time : String,
    val info_content : String
)

data class UserActive(
    val data : List<UserActiveData>,
    val last_page : Int
)
data class UserActiveData(
    val action_ID: Int,
    val avatar: String,
    val content: String,
    val img_IDs: String,
    val img_urls: List<String>,
    val month_rank: Int,
    val signature: String,
    val tags: String,
    val time: String,
    val user_ID: Int,
    val username: String
)
