package com.wingedvampires.attention.model

import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.network.ServiceFactory
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AttentionService {

    @GET("/api/user/getFollowUserVideoAction")
    fun getFollowUserVideoAction(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 5,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<VideoAction>>>

    @GET("/api/user/getRecommendUser")
    fun getRecommendUser(
        @Query("page") page: Int,
        @Query("user_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<List<RecommendUser>>>

    @GET("/api/user/getFuns")
    fun getFans(
        @Query("page") page: Int,
        @Query("user_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<List<Fan>>>

    @GET("/api/user/spotList")
    fun getSpotList(
        @Query("page") page: Int,
        @Query("user_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<List<ConcernPerson>>>

    @GET("/api/search")
    fun search(
        @Query("page") page: Int,
        @Query("content") content: String,
        @Query("user_ID") userId: String = CommonPreferences.userid,
        @Query("limit") limit: Int = 10
    ): Deferred<CommonBody<SearchData>>

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

    @POST("/api/comment/index")
    fun getTotalComments(
        @Query("work_ID") workId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<TotalComments<Comment>>>

    @POST("/api/comment/create")
    fun createComment(
        @Query("work_ID") workId: String,
        @Query("content") content: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    @POST("/api/comment/delete")
    fun deleteComment(
        @Query("comment_ID") commentId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    @POST("/api/comment/update")
    fun updateComment(
        @Query("content") content: String,
        @Query("comment_ID") commentId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    @POST("/api/comment/getCC")
    fun getScecondComments(
        @Query("comment_ID") commentId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<TotalComments<SecondComment>>>

    @POST("/api/comment/createCC")
    fun createSecondComment(
        @Query("comment_ID") commentId: String,
        @Query("content") content: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    @POST("/api/comment/deleteCC")
    fun deleteSecondComment(
        @Query("ccID") ccID: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    @POST("/api/comment/updateCC")
    fun updateSecondComment(
        @Query("content") content: String,
        @Query("ccID") ccID: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<String>>

    @GET("/api/work/getWorkByID")
    fun getWorkByID(
        @Query("work_ID") workId: String,
        @Query("user_ID") userId: String = CommonPreferences.userid
    ): Deferred<CommonBody<List<WorkById>>>

    companion object : AttentionService by ServiceFactory()
}

data class VideoAction(
    val Duration: Float,
    val share_num: String,
    val hot_value: String,
    val avatar: String,
    val collection_num: String,
    val comment_num: String,
    val cover_ID: String,
    val cover_url: String,
    val month_rank: Int,
    val signature: String?,
    val tags: String,
    val time: String,
    val user_ID: String,
    val username: String,
    val video_ID: String,
    val work_ID: String,
    val work_name: String,
    val is_collected: Boolean
)

data class RecommendUser(
    val avatar: String,
    val fans_num: Int,
    val user_ID: String,
    val username: String,
    val signature: String?,
    val tags: String?,
    val month_rank: Int
)

data class Fan(
    val avatar: String,
    val user_ID: String,
    val username: String,
    val month_rank: Int,
    val signature: String?
)

data class ConcernPerson(
    val user_ID: String,
    val age: Int,
    val avatar: String,
    val city: String,
    val fans_num: String,
    val follow_num: String,
    val month_hot_value: Int,
    val sex: String,
    val month_rank: Int,
    val signature: String?,
    val username: String,
    val week_hot_value: Int,
    val year_hot_value: Int,
    val tags: String
)

data class SearchData(
    val user: SearchUser,
    val work: SearchWork
)

data class SearchUser(
    val data: List<DataOfUser>,
    val currentPage: Int,
    val lastPage: Int
)

data class DataOfUser(
    val age: String,
    val avatar: String,
    val city: String,
    val sex: String,
    val signature: String?,
    val username: String,
    val user_ID: String
)

data class SearchWork(
    val data: List<DataOfWork>,
    val currentPage: Int,
    val lastPage: Int
)

data class DataOfWork(
    val hot_value: String,
    val introduction: String,
    val tags: String,
    val time: String,
    val username: String,
    val video_link: String,
    val work_name: String,
    val work_type: Int,
    val work_ID: String
)

data class TotalComments<T>(
    val data: List<T>,
    val currentPage: Int,
    val lastPage: Int
)

data class Comment(
    val avatar: String?,
    val comment_ID: String,
    val content: String,
    val time: String,
    val user_ID: String,
    val comment_comment: Int,
    val username: String
)

data class SecondComment(
    val avatar: String?,
    val ccID: String,
    val content: String,
    val time: String,
    val username: String,
    val user_ID: String
)

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