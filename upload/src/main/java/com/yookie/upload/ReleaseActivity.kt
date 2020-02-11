package com.yookie.upload

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Window
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.vod.upload.VODUploadCallback
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo
import com.alibaba.sdk.android.vod.upload.model.VodInfo
import com.hb.dialog.dialog.LoadingDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.upload.service.UploadImp
import kotlinx.android.synthetic.main.activity_release.*
import pub.devrel.easypermissions.EasyPermissions

class ReleaseActivity : AppCompatActivity() {
    private var selectPicList = mutableListOf<Any>()
    private lateinit var picRecyclerView: RecyclerView
    private lateinit var releasePicAdapter: ReleasePicAdapter
    //    private val picRecyclerViewManager = LinearLayoutManager(this)
    private var imgIdList = ArrayList<String>()
    private var imgs = ""
    var VOD_REGION = "cn-shanghai"
    var VOD_RECORD_UPLOAD_PROGRESS_ENABLED = true
    var imgAuth: ArrayList<String> = ArrayList()
    var imgloadAddress: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_release)
        val imguploader = VODUploadClientImpl(applicationContext)
        imguploader.setRegion(VOD_REGION)
        imguploader.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED)
        var tags = ArrayList<String>()
        selectPicList.add(noSelectPic) // supply a null list
        releasePicAdapter = ReleasePicAdapter(selectPicList, this, this)
        picRecyclerView = findViewById(R.id.release_img)
        val mlayoutManager = GridLayoutManager(this, 3)
        //picRecyclerViewManager.orientation = LinearLayoutManager.HORIZONTAL
        picRecyclerView.apply {
            layoutManager = mlayoutManager
            adapter = releasePicAdapter
        }
        val imgcallback = object : VODUploadCallback() {
            var x = 0

            fun setIndex(index: Int) {
                x = index
            }

            override fun onUploadSucceed(info: UploadFileInfo) {
                OSSLog.logDebug("onsucceed ------------------" + info.filePath)
                if (x== (selectPicList.size - 1)){
                    Log.d("怎么还没成功",x.toString())
                    UploadImp.sendAction(CommonPreferences.userid,release_des.text.toString(),imgs," "){
                        Log.d("擦擦擦","得好好庆祝一番")
                        startActivity(Intent(this@ReleaseActivity,SuccessActivity::class.java))
                    }
                }


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
                imguploader.resumeWithAuth(imgAuth[x])

            }

            override fun onUploadRetry(code: String?, message: String?) {
                OSSLog.logError("onUploadRetry ------------- ")
            }

            override fun onUploadRetryResume() {
                OSSLog.logError("onUploadRetryResume ------------- ")
            }

            override fun onUploadStarted(uploadFileInfo: UploadFileInfo?) {
                OSSLog.logError("onUploadStarted ------------- ")
                imguploader.setUploadAuthAndAddress(uploadFileInfo, imgAuth[x], imgloadAddress[x])
                OSSLog.logError(
                    "file path:" + uploadFileInfo!!.filePath +
                            ", endpoint: " + uploadFileInfo.endpoint +
                            ", bucket:" + uploadFileInfo.bucket +
                            ", object:" + uploadFileInfo.getObject() +
                            ", status:" + uploadFileInfo.status
                )
            }
        }
        imguploader.init(imgcallback)
        imguploader.setPartSize((1024 * 1024).toLong())
        imguploader.setTemplateGroupId("xxx")
        imguploader.setStorageLocation("xxx")

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
            selectPicList.remove(noSelectPic)
            for ((index, value) in selectPicList.withIndex()) {
                    UploadImp.getCoverUpload("jpg") {
                        imgAuth.add(it.UploadAuth)
                        imgloadAddress.add(it.UploadAddress)
                        imgIdList.add(it.ImageId)
                        if (index == (selectPicList.size - 1)) {
                            for (x in imgIdList) {
                                imgs = "$imgs$x,"
                            }
                            for ((indexs, values) in selectPicList.withIndex()) {
                                Log.d("indexindexindex",indexs.toString())
                                imgcallback.setIndex(indexs)
                                imguploader.addFile(
                                    values.toString(),
                                    getImgVodInfo("placeholder", "placeholder")
                                )
                                Log.d("开始传输图片","开始传输图片" + index + "    "+indexs)
                                imguploader.start()
                                imguploader.deleteFile(0)
                            }
                        }
                    }

            }
            var loadingDialog = LoadingDialog(this)
            loadingDialog.setMessage("正在上传")
            loadingDialog.show()


        }

    }

    fun setPicEdit() {
        val list = arrayOf<CharSequence>("更改图片", "删除图片", "取消")
        val alertDialogBuilder = AlertDialog.Builder(this@ReleaseActivity)
        alertDialogBuilder.setItems(list) { dialog, item ->
            when (item) {
                0 -> checkPermAndOpenPic()
                1 -> releasePicAdapter.removePic()
                2 -> dialog.dismiss()
            }
        }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    // request = 2 代表来自于系统相册，requestCode ！= 0 代表选择图片成功
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    Log.d("whatthefuck", selectList[0].path)
                    for (x in selectList) {
                        releasePicAdapter.changePic(x.path) //加载选择的图片
                    }

                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的


                }

            }
        }
    }

    private fun getImgVodInfo(title: String, desc: String): VodInfo {
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
        .maxSelectNum(9)// 最大图片选择数量 int
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

    fun checkPermAndOpenPic() {
        // 检查存储权限
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            EasyPermissions.requestPermissions(
                this,
                "需要外部存储来提供必要的缓存",
                0,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            openSelectPic()
        }
    }
}
