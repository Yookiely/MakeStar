package com.wingedvampires.attention.view

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import com.example.playerlibrary.assist.DataInter
import com.example.playerlibrary.assist.OnVideoViewEventHandler
import com.example.playerlibrary.assist.ReceiverGroupManager
import com.example.playerlibrary.config.PlayerConfig
import com.example.playerlibrary.cover.ControllerCover
import com.example.playerlibrary.entity.DataSource
import com.example.playerlibrary.event.OnPlayerEventListener
import com.example.playerlibrary.provider.MonitorDataProvider
import com.example.playerlibrary.receiver.IReceiver
import com.example.playerlibrary.receiver.ReceiverGroup
import com.example.playerlibrary.render.AspectRatio
import com.example.playerlibrary.render.IRender
import com.example.playerlibrary.widget.BaseVideoView
import com.wingedvampires.attention.R
import com.wingedvampires.attention.extension.PUtil
import com.wingedvampires.attention.model.DataUtils

/**
 * Description:
 * Created by PangHaHa on 18-9-6.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
class TestActivity : AppCompatActivity(), OnPlayerEventListener {

    private var mVideoView: BaseVideoView? = null

    private var margin: Int = 0

    private val permissionSuccess: Boolean = false

    private val typeIndex: Int = 0
    private var mReceiverGroup: ReceiverGroup? = null
    private var isLandscape: Boolean = false

    private var mDataSourceId: Long = 0

    private var userPause: Boolean = false
    private lateinit var user: CardView
    private lateinit var more: LinearLayout

    private val mOnEventAssistHandler = object : OnVideoViewEventHandler() {
        override fun onAssistHandle(assist: BaseVideoView, eventCode: Int, bundle: Bundle) {
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
                    mVideoView!!.setDataSource(generatorDataSource(mDataSourceId))
                    mVideoView!!.start()
                }
                DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN -> requestedOrientation =
                    if (isLandscape)
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    else
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                DataInter.Event.EVENT_CODE_ERROR_SHOW -> mVideoView!!.stop()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.test_video_activity)

        margin = PUtil.dip2px(this, 2f)

        mVideoView = findViewById(R.id.videoView)
        user = findViewById(R.id.cv_videoplay_user)
        more = findViewById(R.id.ll_videoplay_more)
        initPlay()
    }

    private fun initPlay() {
        updateVideo(false)

        mVideoView!!.setOnPlayerEventListener(this)
        mVideoView!!.setEventHandler(mOnEventAssistHandler)
        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this, null)
        mReceiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true)
        mReceiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true)
        mReceiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true)
        mVideoView!!.setReceiverGroup(mReceiverGroup)

        //设置数据提供者 MonitorDataProvider
        val dataProvider = MonitorDataProvider()
        dataProvider.setTestData(DataUtils.getVideoList())
        mVideoView!!.setDataProvider(dataProvider)
        mVideoView!!.setDataSource(generatorDataSource(mDataSourceId))
        mVideoView!!.start()

        // If you want to start play at a specified time,
        // please set this method.
        //mVideoView.start(30*1000);


    }

    private fun generatorDataSource(id: Long): DataSource {
        val dataSource = DataSource()
        dataSource.id = id
        return dataSource
    }

    fun setRenderSurfaceView(view: View) {
        mVideoView!!.setRenderType(IRender.RENDER_TYPE_SURFACE_VIEW)
    }

    fun setRenderTextureView(view: View) {
        mVideoView!!.setRenderType(IRender.RENDER_TYPE_TEXTURE_VIEW)
    }

    fun onStyleSetRoundRect(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoView!!.setRoundRectShape(PUtil.dip2px(this, 25f).toFloat())
        } else {
            Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show()
        }
    }

    fun onStyleSetOvalRect(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoView!!.setOvalRectShape()
        } else {
            Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show()
        }
    }

    fun onShapeStyleReset(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoView!!.clearShapeStyle()
        } else {
            Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show()
        }
    }

    fun onAspect16_9(view: View) {
        mVideoView!!.setAspectRatio(AspectRatio.AspectRatio_16_9)
    }

    fun onAspect4_3(view: View) {
        mVideoView!!.setAspectRatio(AspectRatio.AspectRatio_4_3)
    }

    fun onAspectFill(view: View) {
        mVideoView!!.setAspectRatio(AspectRatio.AspectRatio_FILL_PARENT)
    }

    fun onAspectFit(view: View) {
        mVideoView!!.setAspectRatio(AspectRatio.AspectRatio_FIT_PARENT)
    }

    fun onAspectOrigin(view: View) {
        mVideoView!!.setAspectRatio(AspectRatio.AspectRatio_ORIGIN)
    }

    fun onDecoderChangeMediaPlayer(view: View) {
        val curr = mVideoView!!.currentPosition
        if (mVideoView!!.switchDecoder(PlayerConfig.DEFAULT_PLAN_ID)) {
            replay(curr)
        }
    }


    private fun replay(msc: Int) {
        mVideoView!!.setDataSource(generatorDataSource(mDataSourceId))
        mVideoView!!.start(msc)
    }

    fun removeControllerCover(view: View) {
        mReceiverGroup!!.removeReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER)
        Toast.makeText(this, "已移除", Toast.LENGTH_SHORT).show()
    }

    fun addControllerCover(view: View) {
        val receiver =
            mReceiverGroup!!.getReceiver<IReceiver>(DataInter.ReceiverKey.KEY_CONTROLLER_COVER)
        if (receiver == null) {
            mReceiverGroup!!.addReceiver(
                DataInter.ReceiverKey.KEY_CONTROLLER_COVER,
                ControllerCover(this, true)
            )
            Toast.makeText(this, "已添加", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mVideoView!!.isInPlaybackState) {
            mVideoView!!.pause()
        } else {
            mVideoView!!.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mVideoView!!.isInPlaybackState) {
            if (!userPause)
                mVideoView!!.resume()
        } else {
            mVideoView!!.rePlay(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView!!.stopPlayback()
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
        mReceiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_RESUME -> userPause = false
        }
    }

    private fun updateVideo(landscape: Boolean) {
        val layoutParams = mVideoView!!.layoutParams as LinearLayout.LayoutParams
        if (landscape) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.setMargins(0, 0, 0, 0)
        } else {
            layoutParams.width = PUtil.getScreenW(this) - margin * 2
            layoutParams.height = layoutParams.width * 9 / 16
            layoutParams.setMargins(margin, margin, margin, margin)
        }
        mVideoView!!.layoutParams = layoutParams
    }
}
