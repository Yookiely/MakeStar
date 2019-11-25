package com.example.yangzihang.makestar.network

import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


object UserImp {
    fun getHistoryRank(userid: String,userHistoryCallback: (List<rank>) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getHistoryRank(userid).await()
            if (callback.error_code == -1) {
                userHistoryCallback(callback.data!!)

            }
        }
    }

    fun getMessage(userid: String, model: String, limit: String, page: Int, messageCallback: (List<messageData>) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getMessage(userid, model, limit, page).await()
            if (callback.error_code == -1) {
                messageCallback(callback.data!!.data)

            }

        }
    }

    fun getMessageById(messageid : String, messageCallback: (List<messageData>) -> Unit){

        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getMessageById(messageid).await()
            if (callback.error_code == -1) {
                messageCallback(callback.data!!)

            }

        }
    }

    fun setMessageRead(messageid: String,resultCallback : (result) -> Unit){
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.setMessageRead(messageid).await()
            if (callback.error_code == -1) {
               resultCallback(callback)
            }

        }
    }

    fun getCollection(limit: Int,page: Int,userid: String,collectionCallback : (List<collectionData>) -> Unit){
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getCollection(limit,page,userid).await()
            if (callback.error_code == -1) {
                collectionCallback(callback.data!!.data)
            }

        }

    }}