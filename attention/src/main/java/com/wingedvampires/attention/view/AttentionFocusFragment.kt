package com.wingedvampires.attention.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.view.items.videoActionItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class AttentionFocusFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    var activity: AttentionActivity? = null
    private var itemManager: ItemManager = ItemManager()
    lateinit var attentionRefresh: SwipeRefreshLayout
    private var isLoading = true
    private var page: Int = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attention_focus, container, false)
        attentionRefresh = view.findViewById(R.id.sl_attention_focus)
        val mLayoutManager = LinearLayoutManager(this.context)

        recyclerView = view.findViewById(R.id.rv_attention_focus)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }

        loadVideoActions()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++

                    loadMoreVideoActions()

                }
            }
        })

        attentionRefresh.setOnRefreshListener(this::loadVideoActions)
        return view
    }

    private fun loadVideoActions() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            itemManager.refreshAll { }
            val videoActions = AttentionService.getFollowUserVideoAction(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionFocusFragment.context, "加载关注失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                videoActions.forEach { videoAction ->
                    videoActionItem(
                        activity!!,
                        videoAction,
                        this@AttentionFocusFragment.context!!
                    ) {
                        CommonPreferences.setAndGetUserHistory(videoAction.work_ID)
                        val intent = Intent().also {
                            it.putExtra(
                                AttentionUtils.VIDEO_PALY_WORKID,
                                videoAction.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(activity!!, "VideoPlayActivity", intent)
                    }
                }
            }
            isLoading = false
            attentionRefresh.isRefreshing = false
        }
    }

    private fun loadMoreVideoActions() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val videoActions = AttentionService.getFollowUserVideoAction(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionFocusFragment.context, "加载关注失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                videoActions.forEach { videoAction ->
                    videoActionItem(
                        activity!!,
                        videoAction,
                        this@AttentionFocusFragment.context!!
                    ) {
                        CommonPreferences.setAndGetUserHistory(videoAction.work_ID)
                        val intent = Intent().also {
                            it.putExtra(
                                AttentionUtils.VIDEO_PALY_WORKID,
                                videoAction.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(activity!!, "VideoPlayActivity", intent)
                    }
                }
            }
            isLoading = false
        }
    }
}