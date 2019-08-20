package com.wingedvampires.attention.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.example.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.example.common.experimental.extensions.awaitAndHandle
import com.example.common.experimental.preference.CommonPreferences
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.view.items.videoActionItem
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class AttentionFocusFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val layoutManager = LinearLayoutManager(this.context)
    private var itemManager: ItemManager = ItemManager() //by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    private var page: Int = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attention_focus, container, false)

        recyclerView = view.findViewById(R.id.rv_attention_focus)
        recyclerView.apply {
            layoutManager = layoutManager
            adapter = ItemAdapter(itemManager)
        }

        loadVideoActions()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++

                    loadMoreVideoActions()

                }
            }
        })
        isLoading = false

        return view
    }

    private fun loadVideoActions() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            val videoActions = AttentionService.getFollowUserVideoAction(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionFocusFragment.context, "加载关注失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                videoActions.forEach { videoAction ->
                    videoActionItem(videoAction) {
                        CommonPreferences.setAndGetUserHistory(videoAction.work_ID.toString())
                    }
                }
            }

        }
    }

    private fun loadMoreVideoActions() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val videoActions = AttentionService.getFollowUserVideoAction(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionFocusFragment.context, "加载关注失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                clear()
                videoActions.forEach { videoAction ->
                    videoActionItem(videoAction) {
                        CommonPreferences.setAndGetUserHistory(videoAction.work_ID.toString())
                    }
                }
            }

        }
    }
}