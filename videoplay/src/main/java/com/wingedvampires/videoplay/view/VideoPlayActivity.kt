package com.wingedvampires.videoplay.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hb.dialog.dialog.LoadingDialog
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.wingedvampires.videoplay.R
import com.wingedvampires.videoplay.model.NumberOfStar
import com.wingedvampires.videoplay.model.VideoInfo
import com.wingedvampires.videoplay.model.VideoPlayService
import com.wingedvampires.videoplay.model.VideoPlayUtils
import com.yookie.common.experimental.extensions.ComplaintType
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.ShareMethod
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.android.synthetic.main.activity_simple_play.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class VideoPlayActivity : AppCompatActivity() {

    var backFlag = true // 资源未加载前进行返回的标志
    lateinit var videoPlayer: StandardGSYVideoPlayer
    lateinit var orientationUtils: OrientationUtils
    lateinit var loadingDialog: LoadingDialog
    private var workId: String? = null
    private var isCollected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_simple_play)
        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("加载中...")

        val bundle: Bundle? = intent.extras
        workId = bundle?.getString(VideoPlayUtils.VIDEO_PALY_WORKID)

        if (workId.isNullOrEmpty()) {
            Toast.makeText(this, "视频id错误", Toast.LENGTH_SHORT).show()
            backFlag = false
            onBackPressed()
        }

        videoPlayer = findViewById(R.id.video_player)
        orientationUtils = OrientationUtils(this, videoPlayer)
        init()
        writeOneWatch()
    }

    private fun init() {
        launch(UI + QuietCoroutineExceptionHandler) {
            loadingDialog.show()
            val workOrNot = VideoPlayService.getWorkByID(workId!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            } ?: return@launch
            if (workOrNot.error_code != -1) {
                Toast.makeText(this@VideoPlayActivity, workOrNot.message, Toast.LENGTH_SHORT).show()
                onBackPressed()
                return@launch
            }
            val work = workOrNot.data?.get(0) ?: return@launch

            val videoInfo = VideoPlayService.getVideoInfo(work.video_ID).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }?.data ?: return@launch

            val isFollow = VideoPlayService.isFollow(work.user_ID).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "用户信息加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }?.data ?: return@launch



            if (videoInfo.PlayInfoList.PlayInfo.isEmpty()) {
                Toast.makeText(this@VideoPlayActivity, "视频Url获取失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                onBackPressed()
                return@launch
            }
            isCollected = work.is_collected
            initPlay(videoInfo)
            loadingDialog.dismiss()
            Glide.with(this@VideoPlayActivity).load(work.avatar).error(R.drawable.ms_no_pic)
                .into(cv_videoplay_avatar)
            tv_videoplay_name.text = work.username
            tv_attention_number.text = VideoPlayUtils.format(work.hot_value)
            tv_attention_comment.text = VideoPlayUtils.format(work.comment_num)
            tv_attention_share.text = VideoPlayUtils.format(work.share_num)
            tv_attention_store.text = VideoPlayUtils.format(work.collection_num)

            cv_videoplay_complain.setOnClickListener {
                val intent = Intent()
                intent.putExtra(ComplaintType.COMPLAINT_TYPE, ComplaintType.WORK)
                intent.putExtra(ComplaintType.COMPLAINT_ID, work.work_ID)
                Transfer.startActivityWithoutClose(
                    this@VideoPlayActivity,
                    "ComplaintActivity",
                    intent
                )
            }
            cv_videoplay_avatar.setOnClickListener {
                val intent = Intent()
                intent.putExtra("userID", work.user_ID)
                Transfer.startActivityWithoutClose(this@VideoPlayActivity, "MyselfActivity", intent)
            }
            iv_attention_store.apply {
                if (isCollected)
                    setImageResource(R.drawable.ms_red_star)
                else
                    setImageResource(R.drawable.ms_star)
            }

            iv_attention_store.setOnClickListener {
                it.isEnabled = false
                launch(UI + QuietCoroutineExceptionHandler) {
                    if (!isCollected) {
                        val resultCommonBody =
                            VideoPlayService.addCollection(workId!!).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(
                                    this@VideoPlayActivity,
                                    "收藏失败：${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            resultCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (resultCommonBody.error_code == -1) {
                            iv_attention_store.setImageResource(R.drawable.ms_red_star)
                            isCollected = true
                        }
                    } else {
                        val resultCommonBody =
                            VideoPlayService.deleteCollection(workId!!).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(
                                    this@VideoPlayActivity,
                                    "收藏失败：${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            resultCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (resultCommonBody.error_code == -1) {
                            iv_attention_store.setImageResource(R.drawable.ms_star)
                            isCollected = false
                        }
                    }

                    refreshVideoInfo() {
                        it.isEnabled = true
                    }
                }
            }
            tv_videoplay_add.text = if (isFollow) "取消关注" else "+关注"
            tv_videoplay_add.setOnClickListener { v ->
                v.isEnabled = false
                if (isFollow) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody =
                            VideoPlayService.deleteFollow(work.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@VideoPlayActivity, "操作失败", Toast.LENGTH_SHORT)
                                    .show()
                                v.isEnabled = true
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            addCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody =
                            VideoPlayService.addFollow(work.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@VideoPlayActivity, "操作失败", Toast.LENGTH_SHORT)
                                    .show()
                                v.isEnabled = true
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            addCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                refreshVideoInfo() {
                    v.isEnabled = true
                }
            }
            iv_attention_comment.setOnClickListener {
                val intent = Intent()
                intent.putExtra(VideoPlayUtils.VIDEO_PALY_COMMENT, workId)
                Transfer.startActivityWithoutClose(
                    this@VideoPlayActivity,
                    "CommentsActivity",
                    intent
                )
            }
            iv_attention_number.setOnClickListener { view ->
                launch(UI + QuietCoroutineExceptionHandler) {
                    view.isEnabled = false
                    val commbody = VideoPlayService.star(work.work_ID).awaitAndHandle {
                        it.printStackTrace()
                        Toast.makeText(view.context, "点赞失败", Toast.LENGTH_SHORT).show()
                        view.isEnabled = true
                    }

                    Toast.makeText(view.context, "${commbody?.message}", Toast.LENGTH_SHORT).show()

                    if (commbody?.error_code == -1) {
                        val num = commbody.data as NumberOfStar
                        tv_attention_number.text = VideoPlayUtils.format(num.numberOfStar)
                    }

                    refreshVideoInfo() {
                        view.isEnabled = true
                    }
                }

            }
            iv_attention_share.setOnClickListener {

                ShareMethod.showDialog(
                    this@VideoPlayActivity,
                    workId!!,
                    work.work_name,
                    work.username
                )
                refreshVideoInfo() {}
            }

        }

    }

    private fun refreshVideoInfo(block: () -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {

            val workOrNot = VideoPlayService.getWorkByID(workId!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            } ?: return@launch
            if (workOrNot.error_code != -1) {
                Toast.makeText(this@VideoPlayActivity, workOrNot.message, Toast.LENGTH_SHORT).show()
                onBackPressed()
                return@launch
            }
            val work = workOrNot.data?.get(0) ?: return@launch

            val videoInfo = VideoPlayService.getVideoInfo(work.video_ID).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }?.data ?: return@launch

            val isFollow = VideoPlayService.isFollow(work.user_ID).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "用户信息加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }?.data ?: return@launch

            if (videoInfo.PlayInfoList.PlayInfo.isEmpty()) {
                Toast.makeText(this@VideoPlayActivity, "视频Url获取失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                return@launch
            }
            isCollected = work.is_collected

            Glide.with(this@VideoPlayActivity).load(work.avatar).error(R.drawable.ms_no_pic)
                .into(cv_videoplay_avatar)
            tv_videoplay_name.text = work.username
            tv_attention_number.text = VideoPlayUtils.format(work.hot_value)
            tv_attention_comment.text = VideoPlayUtils.format(work.comment_num)
            tv_attention_share.text = VideoPlayUtils.format(work.share_num)
            tv_attention_store.text = VideoPlayUtils.format(work.collection_num)

            cv_videoplay_complain.setOnClickListener {
                val intent = Intent()
                intent.putExtra(ComplaintType.COMPLAINT_TYPE, ComplaintType.WORK)
                intent.putExtra(ComplaintType.COMPLAINT_ID, work.work_ID)
                Transfer.startActivityWithoutClose(
                    this@VideoPlayActivity,
                    "ComplaintActivity",
                    intent
                )
            }
            cv_videoplay_avatar.setOnClickListener {
                val intent = Intent()
                intent.putExtra("userID", work.user_ID)
                Transfer.startActivityWithoutClose(this@VideoPlayActivity, "MyselfActivity", intent)
            }
            iv_attention_store.apply {
                if (isCollected)
                    setImageResource(R.drawable.ms_red_star)
                else
                    setImageResource(R.drawable.ms_star)
            }
            iv_attention_store.setOnClickListener { v ->
                launch(UI + QuietCoroutineExceptionHandler) {
                    v.isEnabled = false
                    if (!isCollected) {
                        val resultCommonBody =
                            VideoPlayService.addCollection(workId!!).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(
                                    this@VideoPlayActivity,
                                    "收藏失败：${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                v.isEnabled = true
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            resultCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (resultCommonBody.error_code == -1) {
                            iv_attention_store.setImageResource(R.drawable.ms_red_star)
                            isCollected = true
                        }
                    } else {
                        val resultCommonBody =
                            VideoPlayService.deleteCollection(workId!!).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(
                                    this@VideoPlayActivity,
                                    "收藏失败：${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                v.isEnabled = true
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            resultCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (resultCommonBody.error_code == -1) {
                            iv_attention_store.setImageResource(R.drawable.ms_star)
                            isCollected = false
                        }
                    }

                    refreshVideoInfo() {
                        v.isEnabled = true
                    }
                }
            }
            tv_videoplay_add.text = if (isFollow) "取消关注" else "+关注"
            tv_videoplay_add.setOnClickListener { v ->
                v.isEnabled = false
                if (isFollow) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody =
                            VideoPlayService.deleteFollow(work.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@VideoPlayActivity, "操作失败", Toast.LENGTH_SHORT)
                                    .show()
                                v.isEnabled = true
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            addCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody =
                            VideoPlayService.addFollow(work.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@VideoPlayActivity, "操作失败", Toast.LENGTH_SHORT)
                                    .show()
                                v.isEnabled = true
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            addCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                refreshVideoInfo() {
                    v.isEnabled = true
                }
            }
            iv_attention_comment.setOnClickListener {
                val intent = Intent()
                intent.putExtra(VideoPlayUtils.VIDEO_PALY_COMMENT, workId)
                Transfer.startActivityWithoutClose(
                    this@VideoPlayActivity,
                    "CommentsActivity",
                    intent
                )
            }
            iv_attention_number.setOnClickListener { view ->
                launch(UI + QuietCoroutineExceptionHandler) {
                    view.isEnabled = false
                    val commbody = VideoPlayService.star(work.work_ID).awaitAndHandle {
                        it.printStackTrace()
                        Toast.makeText(view.context, "点赞失败", Toast.LENGTH_SHORT).show()
                        view.isEnabled = true
                    }

                    Toast.makeText(view.context, "${commbody?.message}", Toast.LENGTH_SHORT).show()

                    if (commbody?.error_code == -1) {
                        val num = commbody.data as NumberOfStar
                        tv_attention_number.text = VideoPlayUtils.format(num.numberOfStar)
                    }

                    refreshVideoInfo() {
                        view.isEnabled = true
                    }
                }

            }


            iv_attention_share.setOnClickListener {
                ShareMethod.showDialog(
                    this@VideoPlayActivity,
                    workId!!,
                    work.work_name,
                    work.username
                )
            }

            loadingDialog.dismiss()
            block()
        }

    }

    private fun initPlay(videoInfo: VideoInfo) {
        videoPlayer.visibility = View.VISIBLE
        val source1 =
            videoInfo.PlayInfoList.PlayInfo[0].PlayURL
        videoPlayer.setUp(
            source1,
            true,
            videoInfo.VideoBase.Title
        )
        //增加封面
//        ImageView imageView = new ImageView(this);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setImageResource(R.mipmap.xxx1);
//        videoPlayer.setThumbImageView(imageView);
//增加title

        videoPlayer.titleTextView.visibility = View.VISIBLE
        //设置返回键
        videoPlayer.backButton.visibility = View.VISIBLE
        //设置旋转
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.fullscreenButton
            .setOnClickListener { orientationUtils.resolveByClick() }
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true)
        //设置返回按键功能
        videoPlayer.backButton.setOnClickListener { onBackPressed() }
        videoPlayer.startPlayLogic()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        orientationUtils.releaseListener()
    }

    override fun onBackPressed() { //先返回正常状态
        if (backFlag) {
            if (orientationUtils.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                videoPlayer.fullscreenButton.performClick()
                return
            }
            //释放所有
            videoPlayer.setVideoAllCallBack(null)
        }

        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cl_videoplay_more.visibility = View.GONE
            cl_videoplay_user_total.visibility = View.GONE
        } else {
            cl_videoplay_more.visibility = View.VISIBLE
            cl_videoplay_user_total.visibility = View.VISIBLE
        }
    }

    private fun writeOneWatch() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = VideoPlayService.watchOneTime().awaitAndHandle {
                it.printStackTrace()
            }
        }
    }
}