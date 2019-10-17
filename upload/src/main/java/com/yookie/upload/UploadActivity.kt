package com.yookie.upload

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.vod.upload.VODUploadCallback
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl
import com.alibaba.sdk.android.vod.upload.common.UploadStateType
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo
import com.alibaba.sdk.android.vod.upload.model.VodInfo
import com.yookie.upload.service.UploadImp
import kotlinx.android.synthetic.main.activity_upload_upload.*
import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {
    lateinit var coverId: String
    lateinit var coverUrl: String
    var tag: String = ""
    lateinit var uploadAuth: String
    lateinit var uploadAddress: String
    var VOD_REGION = "cn-shanghai"
    var VOD_RECORD_UPLOAD_PROGRESS_ENABLED = true
    lateinit var vodUploadList: List<ItemInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_upload)
        val fileName = intent.getStringExtra("fileName")
        val uploader = VODUploadClientImpl(applicationContext)
        uploader.setRegion(VOD_REGION)
        uploader.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED)
        val callback = object : VODUploadCallback() {

            override fun onUploadSucceed(info: UploadFileInfo) {
                OSSLog.logDebug("onsucceed ------------------" + info.filePath)
                for (i in 0 until vodUploadList.size) {
                    if (vodUploadList[i].file == info.filePath) {
                        if (vodUploadList[i].status === UploadStateType.SUCCESS.toString()) {
                            // 传了同一个文件，需要区分更新
                            continue
                        }
                        vodUploadList[i].progress = 100
                        vodUploadList[i].status = info.status.toString()
                        break
                    }
                }
            }

            override fun onUploadFailed(info: UploadFileInfo, code: String?, message: String?) {
                OSSLog.logError("onfailed ------------------ " + info.filePath + " " + code + " " + message)
                for (i in 0 until vodUploadList.size) {
                    if (vodUploadList[i].file == info.filePath) {
                        vodUploadList[i].status = info.status.toString()
                        break
                    }
                }
            }

            override fun onUploadProgress(
                info: UploadFileInfo,
                uploadedSize: Long,
                totalSize: Long
            ) {
                OSSLog.logDebug("onProgress ------------------ " + info.filePath + " " + uploadedSize + " " + totalSize)
                for (i in 0 until vodUploadList.size) {
                    if (vodUploadList[i].file == info.filePath) {
                        if (vodUploadList[i].status === UploadStateType.SUCCESS.toString()) {
                            // 传了同一个文件，需要区分更新
                            continue
                        }
                        vodUploadList[i].progress = uploadedSize * 100 / totalSize
                        vodUploadList[i].status = info.status.toString()
                        break
                    }
                }
            }

            override fun onUploadTokenExpired() {
                OSSLog.logError("onExpired ------------- ")
                    // 实现时，重新获取上传凭证:UploadAuth
                UploadImp.getVideoUpload(upload_title.text.toString(), fileName,upload_describe.text.toString(), coverId, tag) {
                    uploadAuth = it.UploadAuth
                    uploadAddress = it.UploadAddress
                }
                    uploader.resumeWithAuth(uploadAuth)

            }

            override fun onUploadRetry(code: String?, message: String?) {
                OSSLog.logError("onUploadRetry ------------- ")
            }

            override fun onUploadRetryResume() {
                OSSLog.logError("onUploadRetryResume ------------- ")
            }

            override fun onUploadStarted(uploadFileInfo: UploadFileInfo?) {
                OSSLog.logError("onUploadStarted ------------- ")
                    uploader.setUploadAuthAndAddress(uploadFileInfo, uploadAuth, uploadAddress)
                OSSLog.logError(
                    "file path:" + uploadFileInfo!!.filePath +
                            ", endpoint: " + uploadFileInfo.endpoint +
                            ", bucket:" + uploadFileInfo.bucket +
                            ", object:" + uploadFileInfo.getObject() +
                            ", status:" + uploadFileInfo.status
                )
            }
        }
        uploader.init(callback)
        uploader.setPartSize((1024 * 1024).toLong())
        uploader.setTemplateGroupId("xxx")
        uploader.setStorageLocation("xxx")
        var tags = ArrayList<String>()
        UploadImp.getCoverUpload("jpg") {
            coverId = it.ImageId
            coverUrl = it.ImageURL

        }
        upload_tag_theatre.setOnClickListener {
            if (tags.contains("小剧场")) {
                tags.remove("小剧场")
            } else {
                tags.add("小剧场")
            }
        }
        upload_tag_funny.setOnClickListener {
            if (tags.contains("搞笑")) {
                tags.remove("搞笑")
            } else {
                tags.add("搞笑")
            }
        }

        upload_tag_music.setOnClickListener {
            if (tags.contains("音乐")) {
                tags.remove("音乐")
            } else {
                tags.add("音乐")
            }
        }

        upload_tag_dance.setOnClickListener {
            if (tags.contains("舞蹈")) {
                tags.remove("舞蹈")
            } else {
                tags.add("舞蹈")
            }
        }

        upload_tag_appearance.setOnClickListener {
            if (tags.contains("颜值")) {
                tags.remove("颜值")
            } else {
                tags.add("颜值")
            }
        }

        upload_tag_other.setOnClickListener {
            if (tags.contains("其他")) {
                tags.remove("其他")
            } else {
                tags.add("其他")
            }
        }

        upload_button_up.setOnClickListener {
            val title = upload_title.text.toString()
            val description = upload_describe.text.toString()
            for (x in tags) {
                tag += x
            }
            UploadImp.getVideoUpload(title, fileName, description, coverId, tag) {
                uploadAuth = it.UploadAuth
                uploadAddress = it.UploadAddress
            }
            uploader.addFile(fileName,getVodInfo(title,description,coverUrl,tags))
            uploader.start()

        }


    }
    private fun getVodInfo(title : String,desc : String,coverUrl : String, tags : ArrayList<String>): VodInfo {
        val vodInfo = VodInfo()
        vodInfo.title = title
        vodInfo.desc = desc
        vodInfo.cateId = 1
        vodInfo.isProcess = true
        vodInfo.coverUrl = coverUrl
        vodInfo.tags = tags
            vodInfo.isShowWaterMark = false
            vodInfo.priority = 7
        return vodInfo
    }
}
