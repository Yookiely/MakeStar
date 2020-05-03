package com.example.yangzihang.makestar.network

import android.util.Log
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


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
        messageCallback: (message) -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getMessage(userid, model, limit, page).await()
            if (callback.error_code == -1) {
                messageCallback(callback.data!!)
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

    fun setAllCommentRead(resultCallback: (result) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.setAllCommentRead().await()
            if (callback.error_code == -1) {
                resultCallback(callback)
            }

        }
    }

    fun setAllStarRead(resultCallback: (result) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.setAllStarRead().await()
            if (callback.error_code == -1) {
                resultCallback(callback)
            }

        }
    }

    fun setAllMessageRead(resultCallback: (result) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.setAllMessageRead().await()
            if (callback.error_code == -1) {
                resultCallback(callback)
            }

        }
    }

    fun getMessageNum(userid: String, numCallback: (NewMessage) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getNewMessageCount(userid).await()
            if (callback.error_code == -1) {
                numCallback(callback.data!!)

            }
        }
    }

    fun getAllMessage(page: Int, limit: Int, userid: String, messageCallBack: (Comment) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getAllMessage(page, limit, userid).await()
            if (callback.error_code == -1) {
                messageCallBack(callback.data!!)
            }
        }
    }

    fun getCollection(
        limit: Int,
        page: Int,
        userid: String,
        collectionCallback: (collection) -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getCollection(limit, page, userid).await()
            if (callback.error_code == -1) {
                collectionCallback(callback.data!!)
            }

        }

    }


    fun getStar(limit: Int, page: Int, userid: String, starList: (StarMessage) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getAllStar(page, limit, userid).await()
            if (callback.error_code == -1) {
                starList(callback.data!!)
            }
        }

    }

    fun getFandomInfo(userid: Int, fansCallback: (FandomInfo) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getFandomInfo(userid).await()
            if (callback.error_code == -1) {
                callback.data?.let { fansCallback(it) }
            }
        }
    }

    fun getFandomList(fansListCallback: (FandomListData) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getUserFandomList().await()
            if (callback.error_code == -1) {
                callback.data?.let { fansListCallback(it) }
            }
        }
    }

    fun changeStatusOfFandom(userid: Int, statuscallback: suspend (Int) -> (Unit)) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val params = mapOf(
                "user_ID" to CommonPreferences.userid,
                "host_user_ID" to userid.toString()
            )
            val callback = UserService.changeStatusOfFandom(params).await()
            statuscallback(callback.error_code)
        }
    }

    fun getRecentActions(
        limit: Int,
        page: Int,
        hostUserId: Int,
        actionsListCallback: (FansCircleInfo) -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getRecentActions(limit, page, hostUserId).await()
            if (callback.error_code == -1) {
                callback.data?.let { actionsListCallback(it) }
            }
        }
    }


    fun getUserActions(
        limit: Int,
        page: Int,
        hostUserId: Int,
        actionsListCallback: (UserActive) -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getUserActions(limit, page, hostUserId).await()
            if (callback.error_code == -1) {
                callback.data?.let { actionsListCallback(it) }
            }
        }
    }

    fun getActionInfo(actionId: String, block: (DetailAction) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getActionById(actionId).awaitAndHandle {
                it.printStackTrace()
            }
            if (callback?.error_code == -1) {
                callback.data?.let {
                    if (it.isNotEmpty()) {
                        block(it[0])
                    }
                }
            }
        }
    }


    fun getCommemtByActionID(
        fandomId: String,
        limit: Int,
        page: Int,
        commentCallback: (FansComment) -> (Unit)
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getCommemtByActionID(fandomId, limit, page).await()
            if (callback.error_code == -1) {
                callback.data?.let { commentCallback(it) }
            }
        }
    }

    fun getCommentCommentByAcID(
        facId: Int,
        limit: Int,
        page: Int,
        secommentCallback: (FansSeComment) -> (Unit)
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getCommentCommentByAcID(facId, limit, page).await()
            if (callback.error_code == -1) {
                callback.data?.let { secommentCallback(it) }
            }
        }
    }

    fun getMyVideoList(page: Int, userid: String, listcallback: (MyVideoList) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getmyVideoList(userid, page, 10).await()
            if (callback.error_code == -1) {
                listcallback(callback.data!!)
            }
        }
    }


    fun getUserInfo(userid: String, UserInfoCallback: (UserInfo) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getUserInfo(userid).awaitAndHandle {
                it.printStackTrace()
            } ?: return@launch
            Log.d("userinfo", callback.error_code.toString())
            if (callback.error_code == -1) {
                UserInfoCallback(callback.data!!)
            }
        }
    }


    fun deleteCollection(collectionId: Int, deleteCallback: () -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.deleteCollection(collectionId).await()
            if (callback.error_code == -1) {
                deleteCallback()
            }

        }
    }


    fun deleteWork(workID: String, deleteCallback: () -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.deleteWorkByID(workID).await()
            if (callback.error_code == -1) {
                deleteCallback()
            }

        }
    }

    fun deleteAction(actionID: String, deleteCallback: () -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.deleteActionByID(actionID).await()
            if (callback.error_code == -1) {
                deleteCallback()
            }

        }
    }

    fun getUserAgreement(agreeCallBack: (String) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getUserAgreement().await()
            if (callback.error_code == -1) {
                agreeCallBack(callback.data!!.info_content)
            }
        }
    }

    fun getPrivateAgreement(agreeCallBack: (String) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = UserService.getPrivateAgreement().await()
            if (callback.error_code == -1) {
                agreeCallBack(callback.data!!.info_content)
            }
        }
    }

    fun sendMessage(
        from: String,
        to: String,
        content: String,
        userid: String,
        sendMessageCallback: () -> Unit
    ) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val params = mapOf(
                "from" to from,
                "to" to to,
                "content" to content,
                "user_ID" to userid
            )
            val callback = UserService.sendMessage(params).await()
            if (callback.error_code == -1) {
                sendMessageCallback()
            }
        }
    }

}