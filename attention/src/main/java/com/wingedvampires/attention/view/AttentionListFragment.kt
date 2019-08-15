package com.wingedvampires.attention.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.example.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.example.common.experimental.extensions.awaitAndHandle
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class AttentionListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val layoutManager = LinearLayoutManager(this.context)
    private var itemManager: ItemManager = ItemManager() //by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    private var page = 1
    private var showSearch = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attention_list, container, false)

        recyclerView = view.findViewById(R.id.rv_attention_list)
        recyclerView.apply {
            layoutManager = layoutManager
            adapter = ItemAdapter(itemManager)
        }
        val recommendText = view.findViewById<TextView>(R.id.tv_attention_recommend_list)
        val focusText = view.findViewById<TextView>(R.id.tv_attention_focus_list)
        val fansText = view.findViewById<TextView>(R.id.tv_attention_fans_list)
        var refreshList: () -> Unit = { loadRecommend() }
        var loadMoreList: () -> Unit = {}
        val searchEdit = view.findViewById<EditText>(R.id.et_attention_search)

        refreshList()

        searchEdit.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {

            } else {

            }
        }

        recommendText.setOnClickListener {
            loadRecommend()
            refreshList = { loadRecommend() }
            loadMoreList = { loadMoreRecommend() }
        }

        fansText.setOnClickListener {
            loadFans()
            refreshList = { loadFans() }
            loadMoreList = { loadMoreFans() }
        }

        focusText.setOnClickListener {
            loadFocus()
            refreshList = { loadFocus() }
            loadMoreList = { loadMoreFocus() }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++
                    loadMoreList()

                }
            }
        })

        isLoading = false

        return view
    }

    private fun loadRecommend() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            val recommendUsers = AttentionService.getRecommendUser(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                recommendUsers.forEach { recommendUser ->
                    recommendItem(recommendUser) {

                    }
                }
            }
        }
    }

    private fun loadMoreRecommend() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val recommendUsers = AttentionService.getRecommendUser(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                recommendUsers.forEach { recommendUser ->
                    recommendItem(recommendUser) {

                    }
                }
            }
        }
    }

    private fun loadFans() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            val fans = AttentionService.getFans(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "粉丝加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                fans.forEach { fan ->
                    fansItem(fan) {

                    }
                }
            }
        }
    }

    private fun loadMoreFans() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val fans = AttentionService.getFans(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "粉丝加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                fans.forEach { fan ->
                    fansItem(fan) {

                    }
                }
            }
        }
    }

    private fun loadFocus() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            val concernPersons = AttentionService.getSpotList(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                concernPersons.forEach { concernPerson ->
                    focusItem(concernPerson) {

                    }
                }
            }
        }
    }

    private fun loadMoreFocus() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val concernPersons = AttentionService.getSpotList(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                concernPersons.forEach { concernPerson ->
                    focusItem(concernPerson) {

                    }
                }
            }
        }
    }

}