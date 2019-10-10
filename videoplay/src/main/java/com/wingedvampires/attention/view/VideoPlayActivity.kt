package com.wingedvampires.attention.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.playerlibrary.assist.DataInter
import com.example.playerlibrary.assist.OnVideoViewEventHandler
import com.example.playerlibrary.assist.ReceiverGroupManager
import com.example.playerlibrary.entity.DataSource
import com.example.playerlibrary.event.OnPlayerEventListener
import com.example.playerlibrary.provider.MonitorDataProvider
import com.example.playerlibrary.provider.VideoBean
import com.example.playerlibrary.receiver.ReceiverGroup
import com.example.playerlibrary.widget.BaseVideoView
import com.wingedvampires.attention.R
import com.wingedvampires.attention.extension.PUtil
import com.wingedvampires.attention.model.VideoPlayService
import com.wingedvampires.attention.model.VideoPlayUtils
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.android.synthetic.main.test_video_activity.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Description:
 * Created by PangHaHa on 18-9-6.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
class VideoPlayActivity : AppCompatActivity(), OnPlayerEventListener {

    private lateinit var mVideoView: BaseVideoView
    private var margin: Int = 0
    private var mReceiverGroup: ReceiverGroup? = null
    private var isLandscape: Boolean = false
    private var mDataSourceId: Long = 0
    private var userPause: Boolean = false
    private lateinit var user: CardView
    private lateinit var more: ConstraintLayout
    private var workId: String? = null
    private var videoBeanList = mutableListOf<VideoBean>()
    private var isCollected = false
    private var havaAdd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.test_video_activity)
        val bundle: Bundle? = intent.extras
        workId = bundle?.getString(VideoPlayUtils.VIDEO_PALY_WORKID)
        if (workId.isNullOrEmpty()) {
            Toast.makeText(this, "视频id错误", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        margin = PUtil.dip2px(this, 2f)
        mVideoView = findViewById(R.id.videoView)
        user = findViewById(R.id.cv_videoplay_user)
        more = findViewById(R.id.ll_videoplay_more)

        loadVideoInfo()
        writeOneWatch()
    }

    private fun writeOneWatch() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = VideoPlayService.watchOneTime().awaitAndHandle {
                it.printStackTrace()
            }
        }
    }


    private fun loadVideoInfo() {
        launch(UI + QuietCoroutineExceptionHandler) {

            val work = VideoPlayService.getWorkByID(workId!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
            }?.data?.get(0) ?: return@launch

            val videoInfo = VideoPlayService.getVideoInfo(work.video_ID).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@VideoPlayActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            if (videoInfo.PlayInfoList.PlayInfo.isEmpty()) {
                Toast.makeText(this@VideoPlayActivity, "视频Url获取失败", Toast.LENGTH_SHORT).show()
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
            tv_videoplay_title.text = work.work_name

            iv_attention_store.apply {
                if (isCollected)
                    setImageResource(R.drawable.ms_red_star)
                else
                    setImageResource(R.drawable.ms_star)
            }
            iv_attention_store.setOnClickListener {
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
                }
            }
            tv_videoplay_add.setOnClickListener {
                if (havaAdd) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody =
                            VideoPlayService.deleteFollow(work.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@VideoPlayActivity, "操作失败", Toast.LENGTH_SHORT)
                                    .show()
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            addCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (addCommonBody.error_code == -1) {
                            tv_videoplay_add.text = "+关注"
                            havaAdd = false
                        }
                    }
                } else {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody =
                            VideoPlayService.addFollow(work.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@VideoPlayActivity, "操作失败", Toast.LENGTH_SHORT)
                                    .show()
                            } ?: return@launch

                        Toast.makeText(
                            this@VideoPlayActivity,
                            addCommonBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (addCommonBody.error_code == -1) {
                            tv_videoplay_add.text = "取关"
                            havaAdd = true
                        }
                    }
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

            videoBeanList.clear()
            videoBeanList.add(
                VideoBean(
                    videoInfo.VideoBase.Title,
                    videoInfo.VideoBase.CoverURL,
                    videoInfo.PlayInfoList.PlayInfo[0].PlayURL
                )
            )

            initPlay()
        }
    }

    private fun initPlay() {
        updateVideo(false)

        val mOnEventAssistHandler: OnVideoViewEventHandler =
            object : OnVideoViewEventHandler() {
                override fun onAssistHandle(
                    assist: BaseVideoView?, eventCode: Int, bundle: Bundle?
                ) {
                    super.onAssistHandle(assist, eventCode, bundle)
                    when (eventCode) {
                        DataInter.Event.CODE_REQUEST_PAUSE -> userPause = true
                        DataInter.Event.EVENT_CODE_REQUEST_BACK -> if (isLandscape) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            finish()
                        }
                        DataInter.Event.EVENT_CODE_REQUEST_NEXT -> {
                            mDataSourceId++
                            mVideoView.setDataSource(generatorDataSource(mDataSourceId))
                            mVideoView.start()
                        }
                        DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN -> requestedOrientation =
                            if (isLandscape)
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            else
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        DataInter.Event.EVENT_CODE_ERROR_SHOW -> mVideoView.stop()
                    }
                }
            }

        mVideoView.setOnPlayerEventListener(this)
        mVideoView.setEventHandler(mOnEventAssistHandler)
        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this, null)
        mReceiverGroup?.groupValue?.putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true)
        mReceiverGroup?.groupValue?.putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true)
        mReceiverGroup?.groupValue?.putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true)
        mVideoView.setReceiverGroup(mReceiverGroup)

        //设置数据提供者 MonitorDataProvider
        val dataProvider = MonitorDataProvider()
        dataProvider.setTestData(videoBeanList)
        mVideoView.setDataProvider(dataProvider)
        mVideoView.setDataSource(generatorDataSource(mDataSourceId))
        mVideoView.start()

        // If you want to start play at a specified time,
        // please set this method.
        //mVideoView.start(30*1000);

    }

    private fun generatorDataSource(id: Long): DataSource {
        val dataSource = DataSource()
        dataSource.id = id
        return dataSource
    }

    override fun onPause() {
        super.onPause()
        if (mVideoView.isInPlaybackState) {
            mVideoView.pause()
        } else {
            mVideoView.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mVideoView.isInPlaybackState) {
            if (!userPause)
                mVideoView.resume()
        } else {
            mVideoView.rePlay(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.stopPlayback()
    }

    override fun onBackPressed() {
        if (isLandscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return
        }
        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true
            user.visibility = View.GONE
            more.visibility = View.GONE
            updateVideo(true)
        } else {
            isLandscape = false
            user.visibility = View.VISIBLE
            more.visibility = View.VISIBLE
            updateVideo(false)
        }
        mReceiverGroup?.groupValue?.putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_RESUME -> userPause = false
        }
    }

    private fun updateVideo(landscape: Boolean) {
        val layoutParams = mVideoView.layoutParams as LinearLayout.LayoutParams
        if (landscape) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.setMargins(0, 0, 0, 0)
        } else {
            layoutParams.width = PUtil.getScreenW(this) - margin * 2
            layoutParams.height = layoutParams.width * 9 / 16
            layoutParams.setMargins(margin, margin, margin, margin)
        }
        mVideoView.layoutParams = layoutParams
    }
}