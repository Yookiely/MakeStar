package com.example.yangzihang.makestar.network

import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.security.auth.callback.Callback


object UserImp {
    fun getHistoryRank(userid: String, userHistoryCallback: (List<rank>) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getHistoryRank(userid).await()
            if (callback.error_code == -1) {
                userHistoryCallback(callback.data!!)

            }
        }
    }

    fun getMessage(
        userid: String,
        model: String,
        limit: String,
        page: Int,
        messageCallback: (List<messageData>) -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getMessage(userid, model, limit, page).await()
            if (callback.error_code == -1) {
                messageCallback(callback.data!!.data)
            }

        }
    }

    fun getMessageById(messageid: String, messageCallback: (List<messageData>) -> Unit) {

        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getMessageById(messageid).await()
            if (callback.error_code == -1) {
                messageCallback(callback.data!!)

            }

        }
    }

    fun setMessageRead(messageid: String, resultCallback: (result) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.setMessageRead(messageid).await()
            if (callback.error_code == -1) {
                resultCallback(callback)
            }

        }
    }

    fun getCollection(
        limit: Int,
        page: Int,
        userid: String,
        collectionCallback: (List<collectionData>) -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getCollection(limit, page, userid).await()
            if (callback.error_code == -1) {
                collectionCallback(callback.data!!.data)
            }

        }

    }

    fun getFandomInfo( userid: Int ,fansCallback : (FandomInfo)-> Unit){
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getFandomInfo(userid).await()
            if (callback.error_code == -1) {
                callback.data?.let { fansCallback(it) }
            }
        }
    }
    fun getFandomList(fansListCallback :(FandomListData) -> Unit){
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getUserFandomList().await()
            if (callback.error_code == -1) {
                callback.data?.let { fansListCallback(it) }
            }
        }
    }
    fun changeStatusOfFandom(userid:Int ,statuscallback: suspend (Int) -> (Unit)) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val params = mapOf(
                "user_ID" to CommonPreferences.userid,
                "host_user_ID"  to userid.toString()
            )
            val callback = UserService.changeStatusOfFandom(params).await()
            statuscallback(callback.error_code)
        }
    }
    fun getRecentActions(limit: Int,page: Int,hostUserId :Int,actionsListCallback :(FansCircleInfo) -> Unit){
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getRecentActions(limit,page,hostUserId).await()
            if (callback.error_code == -1) {
                callback.data?.let { actionsListCallback(it) }
            }
        }
    }
    fun getCommemtByActionID(fandomId:Int ,limit: Int,page: Int, commentCallback:(FansComment) ->(Unit)){
        launch(UI+ QuietCoroutineExceptionHandler){
            val callback = UserService.getCommemtByActionID(fandomId,limit,page).await()
            if(callback.error_code==-1){
                callback.data?.let { commentCallback(it)  }
            }
        }
    }
    fun getCommentCommentByAcID(facId : Int ,limit: Int,page: Int, secommentCallback:(FansSeComment) ->(Unit)){
        launch(UI+ QuietCoroutineExceptionHandler){
            val callback = UserService.getCommentCommentByAcID(facId,limit,page).await()
            if(callback.error_code==-1){
                callback.data?.let { secommentCallback(it)  }
            }
        }
    }
}