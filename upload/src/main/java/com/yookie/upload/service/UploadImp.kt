package com.yookie.upload.service

import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

object UploadImp {
    fun getVideoUpload(title : String,fileName : String,description : String,coverID : String,tags : String, videoUploadCallback :(videoUpload) -> Unit){
        launch(UI+ QuietCoroutineExceptionHandler){
            val callback = UploadService.getVideoUpload(title,fileName, description, coverID, tags).await()
            if (callback.error_code==-1){
                videoUploadCallback(callback.data!!)

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