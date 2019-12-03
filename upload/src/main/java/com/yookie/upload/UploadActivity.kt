package com.yookie.upload

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Window
import android.widget.Toast
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.vod.upload.VODUploadCallback
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl
import com.alibaba.sdk.android.vod.upload.common.UploadStateType
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo
import com.alibaba.sdk.android.vod.upload.model.VodInfo
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.yookie.upload.service.UploadImp
import kotlinx.android.synthetic.main.activity_upload_upload.*
import kotlin.collections.ArrayList
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class UploadActivity : AppCompatActivity() {
    lateinit var coverId: String
    lateinit var coverUrl: String
    lateinit var imgpath : File
    var tag: String = ""
    lateinit var uploadAuth: String
    lateinit var uploadAddress: String
    lateinit var imgAuth : String
    lateinit var  imgloadAddress : String
    var VOD_REGION = "cn-shanghai"
    var VOD_RECORD_UPLOAD_PROGRESS_ENABLED = true
    lateinit var vodUploadList: List<ItemInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_upload_upload)
        val fileName = intent.getStringExtra("fileName")
        val uploader = VODUploadClientImpl(applicationContext)
        val imguploader = VODUploadClientImpl(applicationContext)
        imguploader.setRegion(VOD_REGION)
        imguploader.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED)
        uploader.setRegion(VOD_REGION)
        uploader.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED)
        var tags = ArrayList<String>()
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
        val imgcallback = object : VODUploadCallback() {

            override fun onUploadSucceed(info: UploadFileInfo) {
                OSSLog.logDebug("onsucceed ------------------" + info.filePath)

            }

            override fun onUploadFailed(info: UploadFileInfo, code: String?, message: String?) {
                OSSLog.logError("onfailed ------------------ " + info.filePath + " " + code + " " + message)

            }

            override fun onUploadProgress(
                info: UploadFileInfo,
                uploadedSize: Long,
                totalSize: Long
            ) {
                OSSLog.logDebug("onProgress ------------------ " + info.filePath + " " + uploadedSize + " " + totalSize)

            }

            override fun onUploadTokenExpired() {
                OSSLog.logError("onExpired ------------- ")
                // 实现时，重新获取上传凭证:UploadAuth
                //应该不用重新获取
                imguploader.resumeWithAuth(imgAuth)

            }

            override fun onUploadRetry(code: String?, message: String?) {
                OSSLog.logError("onUploadRetry ------------- ")
            }

            override fun onUploadRetryResume() {
                OSSLog.logError("onUploadRetryResume ------------- ")
            }

            override fun onUploadStarted(uploadFileInfo: UploadFileInfo?) {
                OSSLog.logError("onUploadStarted ------------- ")
                imguploader.setUploadAuthAndAddress(uploadFileInfo, imgAuth, imgloadAddress)
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
        imguploader.init(imgcallback)
        imguploader.setPartSize((1024 * 1024).toLong())
        imguploader.setTemplateGroupId("xxx")
        imguploader.setStorageLocation("xxx")

        upload_button.setOnClickListener {
            checkPermAndOpenPic()
        }
        upload_tag_theatre.setOnClickListener {
            if (tags.contains("小剧场")) {
                tags.remove("小剧场")
                it.setBackgroundResource(R.drawable.tag_circle_gray)
            } else {
                tags.add("小剧场")
                it.setBackgroundResource(R.drawable.button_circle_shapes)
            }
        }
        upload_tag_funny.setOnClickListener {
            if (tags.contains("搞笑")) {
                tags.remove("搞笑")
                it.setBackgroundResource(R.drawable.tag_circle_gray)
            } else {
                tags.add("搞笑")
                it.setBackgroundResource(R.drawable.button_circle_shapes)
            }
        }

        upload_tag_music.setOnClickListener {
            if (tags.contains("音乐")) {
                tags.remove("音乐")
                it.setBackgroundResource(R.drawable.tag_circle_gray)
            } else {
                tags.add("音乐")
                it.setBackgroundResource(R.drawable.button_circle_shapes)
            }
        }

        upload_tag_dance.setOnClickListener {
            if (tags.contains("舞蹈")) {
                tags.remove("舞蹈")
                it.setBackgroundResource(R.drawable.tag_circle_gray)
            } else {
                tags.add("舞蹈")
                it.setBackgroundResource(R.drawable.button_circle_shapes)
            }
        }

        upload_tag_appearance.setOnClickListener {
            if (tags.contains("颜值")) {
                tags.remove("颜值")
                it.setBackgroundResource(R.drawable.tag_circle_gray)
            } else {
                tags.add("颜值")
                it.setBackgroundResource(R.drawable.button_circle_shapes)
            }
        }

        upload_tag_other.setOnClickListener {
            if (tags.contains("其他")) {
                tags.remove("其他")
                it.setBackgroundResource(R.drawable.tag_circle_gray)
            } else {
                tags.add("其他")
                it.setBackgroundResource(R.drawable.button_circle_shapes)
            }
        }

        upload_button_up.setOnClickListener {
            if ( upload_title.text.isNotEmpty() && upload_describe.text.isNotEmpty() && imgpath.exists()){
                val title = upload_title.text.toString()
                val description = upload_describe.text.toString()
                for (x in tags) {
                    tag += x
                }
                UploadImp.getVideoUpload(title, fileName, description, coverId, tag) {
                    uploadAuth = it.UploadAuth
                    uploadAddress = it.UploadAddress//获取视频上传证书
                    UploadImp.getCoverUpload("jpg") {
                        coverId = it.ImageId
                        coverUrl = it.ImageURL
                        imgAuth = it.UploadAuth
                        imgloadAddress = it.UploadAddress//获取图片上传证书
                        imguploader.addFile(imgpath.absolutePath,getImgVodInfo(title,description))
                        imguploader.start()
                        uploader.addFile(fileName,getVodInfo(title,description,coverUrl,tags))
                        uploader.start()//可以同时传输吧？？盲猜可以
                    }
                }
            }else if (upload_title.text.isEmpty()){
                Toast.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show()
            }else if (upload_describe.text.isEmpty()){
                Toast.makeText(this, "请填写描述", Toast.LENGTH_SHORT).show()
            }

        }


    }

    // request = 2 代表来自于系统相册，requestCode ！= 0 代表选择图片成功
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    Log.d("whatthefuck",selectList[0].path)
                    Glide.with(this)
                        .load(selectList[0].path)
                        .into(upload_video)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    imgpath=File(selectList[0].path)


                    }

                }
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

    private fun getImgVodInfo(title : String,desc : String): VodInfo {
        val vodInfo = VodInfo()
        vodInfo.title = title
        vodInfo.desc = desc
        vodInfo.cateId = 1
        vodInfo.isProcess = true
        vodInfo.isShowWaterMark = false
        vodInfo.priority = 7
        return vodInfo
    }


    fun openSelectPic() = PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
        .maxSelectNum(1)// 最大图片选择数量 int
        .minSelectNum(1)// 最小选择数量 int
        .imageSpanCount(4)// 每行显示个数 int
        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
        .previewImage(true)// 是否可预览图片 true or false
        .isCamera(true)// 是否显示拍照按钮 true or false
        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
        .forResult(PictureConfig.CHOOSE_REQUEST)
//
    fun checkPermAndOpenPic() {
        // 检查存储权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, "需要外部存储来提供必要的缓存", 0, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            openSelectPic()
        }
    }
}


