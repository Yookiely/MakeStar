package com.wingedvampires.homepage.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageService
import com.wingedvampires.homepage.model.HomePageUtils
import com.wingedvampires.homepage.view.items.searchUserItem
import com.wingedvampires.homepage.view.items.searchVideoItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.android.synthetic.main.activity_search_more.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MoreSearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var lastPage = Int.MAX_VALUE
    private var page = 1
    var refreshList: () -> Unit = {}
    var loadMoreList: () -> Unit = {}
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    var searchContent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_search_more)
        val bundle = intent.extras
        searchContent = bundle.getString(HomePageUtils.SEARCH_CONTENT)
        mSwipeRefreshLayout = findViewById(R.id.sl_search_more)
        val searchType = bundle.getString(HomePageUtils.SEARCH_TYPE)
        val mLayoutManager = LinearLayoutManager(this)
        if (searchContent.isNullOrEmpty() || searchType.isNullOrEmpty()) {
            Toast.makeText(this, "搜索内容有误", Toast.LENGTH_SHORT).show()
        }

        when (searchType) {
            HomePageUtils.SEARCH_USER -> {
                refreshList = { loadSearchForUser() }
                loadMoreList = { loadMoreSearchForUser() }
                tv_search_more_title.text = "搜索结果：用户"
            }
            HomePageUtils.SEARCH_VIDEO -> {
                refreshList = { loadSearchForVideo() }
                loadMoreList = { loadMoreSearchForVideo() }
                tv_search_more_title.text = "搜索结果：视频"
            }
        }

        recyclerView = findViewById(R.id.rv_search_more)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++
                    loadMoreList()
                }
            }
        })

        refreshList()
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh)
    }

    private fun refresh() = refreshList()

    private fun loadSearchForUser() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            itemManager.refreshAll {
            }
            val result = HomePageService.search(page, searchContent!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@MoreSearchActivity, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            lastPage = result.user.lastPage
            val usersOfResult = result.user.data
            itemManager.refreshAll {
                clear()
                usersOfResult.forEach { dataOfUser ->
                    searchUserItem(dataOfUser) {

                    }
                }
            }
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadMoreSearchForUser() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (page > lastPage) {
                Toast.makeText(this@MoreSearchActivity, "没有更多了", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            val result = HomePageService.search(page, searchContent!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@MoreSearchActivity, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            lastPage = result.user.lastPage
            val usersOfResult = result.user.data
            itemManager.autoRefresh {
                usersOfResult.forEach { dataOfUser ->
                    searchUserItem(dataOfUser) {
                    }
                }
            }
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadSearchForVideo() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            itemManager.refreshAll {

            }
            val result = HomePageService.search(page, searchContent!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@MoreSearchActivity, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            lastPage = result.work.lastPage
            val works = result.work.data
            itemManager.refreshAll {
                clear()
                works.forEach { dataOfWork ->
                    searchVideoItem(dataOfWork) {
                        val intent = Intent().also {
                            it.putExtra(
                                HomePageUtils.VIDEO_PALY_WORKID,
                                dataOfWork.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(
                            this@MoreSearchActivity,
                            "VideoPlayActivity",
                            intent
                        )
                    }
                }
            }
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadMoreSearchForVideo() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (page > lastPage) {
                Toast.makeText(this@MoreSearchActivity, "没有更多了", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            val result = HomePageService.search(page, searchContent!!).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@MoreSearchActivity, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            lastPage = result.work.lastPage
            val works = result.work.data
            itemManager.autoRefresh {
                works.forEach { dataOfWork ->
                    searchVideoItem(dataOfWork) {
                        val intent = Intent().also {
                            it.putExtra(
                                HomePageUtils.VIDEO_PALY_WORKID,
                                dataOfWork.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(
                            this@MoreSearchActivity,
                            "VideoPlayActivity",
                            intent
                        )
                    }
                }
            }
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }


}
