package com.yookie.upload

import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.vod.upload.VODUploadCallback
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo
import com.yookie.upload.service.UploadImp

abstract class MVODUploadCallback(val index : Int) : VODUploadCallback() {


}