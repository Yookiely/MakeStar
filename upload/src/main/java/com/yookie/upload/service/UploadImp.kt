package com.yookie.upload.service

import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

object UploadImp {
    fun getVideoUpload(title : String,fileName : String,description : String,coverID : String,tags : String,workType : Int,userId: String, videoUploadCallback :(videoUpload) -> Unit){
        writeOneUpload()
        launch(UI+ QuietCoroutineExceptionHandler){
            val callback = UploadService.getVideoUpload(title,fileName, description, coverID, tags,workType,userId).await()
            if (callback.error_code==-1){
                videoUploadCallback(callback.data!!)

            }
        }
    }

    private fun writeOneUpload() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = UploadService.uploadOneTime().awaitAndHandle {
                it.printStackTrace()
            }
        }
    }

    fun getCoverUpload(ext : String,coverUploadCallback : (coverUpload) -> Unit){
        launch(UI + QuietCoroutineExceptionHandler){
            val callback = UploadService.getCoverUpload(ext).await()
            if (callback.error_code==-1){
                coverUploadCallback(callback.data!!)
            }
        }
    }

    fun sendAction(userId : String,content : String,imgIDs : String,atuser : String,sendCallback : (actionResponse) -> Unit){
        launch(UI + QuietCoroutineExceptionHandler){
            val callback = UploadService.sendAction(userId, content, imgIDs, atuser).await()
            sendCallback(callback)
        }

    }


}