package com.wingedvampires.attention.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.view.items.fansItem
import com.wingedvampires.attention.view.items.focusItem
import com.wingedvampires.attention.view.items.historyItem
import com.wingedvampires.attention.view.items.recommendItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class AttentionListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var page = 1
    var activity: AttentionActivity? = null
    private var lastPage = Int.MAX_VALUE
    private var liveLabel: TextView? = null
    private var backLabel: ImageView? = null
    private lateinit var searchToolbar: ConstraintLayout
    private lateinit var tabs: ConstraintLayout
    var refreshList: () -> Unit = { loadRecommend() }
    var loadMoreList: () -> Unit = {}
    private var restoreUI: () -> Unit = {}
    private lateinit var searchEdit: EditText
    private lateinit var listRefresh: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attention_list, container, false)
        listRefresh = view.findViewById(R.id.sl_attention_list)
        val mLayoutManager = LinearLayoutManager(this.context)
        val clearSearchImg: ImageView = view.findViewById(R.id.iv_attention_clear_history)
        val clearSearchText: TextView = view.findViewById(R.id.tv_attention_clear_history)
        val recommendText = view.findViewById<TextView>(R.id.tv_attention_recommend_list)
        val focusText = view.findViewById<TextView>(R.id.tv_attention_focus_list)
        val fansText = view.findViewById<TextView>(R.id.tv_attention_fans_list)
        searchEdit = view.findViewById(R.id.et_attention_search)
        searchToolbar = view.findViewById(R.id.cl_attention_search_toolbar)
        tabs = view.findViewById(R.id.cl_attention_title_list)
        recyclerView = view.findViewById(R.id.rv_attention_list)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }

        refreshList()

        searchEdit.setOnFocusChangeListener { _, hasFocus ->
            tabs.visibility = View.GONE

            backLabel?.visibility = View.VISIBLE
            liveLabel?.visibility = View.GONE

            if (hasFocus) {
                if (searchEdit.text.isBlank()) {
                    showHistory()
                    refreshList = { showHistory() }
                }
            }
        }

        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && searchEdit.text.isNotBlank()) {
                loadSearch()
                searchEdit.isFocusable = false
                searchEdit.isFocusableInTouchMode = true
                activity?.hideSoftInputMethod()
            }

            true
        }

        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isBlank()) {
                    showHistory()
                }
            }

        })

        // 清除历史
        clearSearchImg.setOnClickListener {
            AttentionUtils.searchHistory = mutableListOf()
            showHistory()
        }
        clearSearchText.setOnClickListener {
            AttentionUtils.searchHistory = mutableListOf()
            showHistory()
        }

        backLabel?.setOnClickListener {
            refreshView()
        }

        recommendText.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recommendText.setTextAppearance(R.style.SelectedFocusListText)
                fansText.setTextAppearance(R.style.FocusListText)
                focusText.setTextAppearance(R.style.FocusListText)
            }
            loadRecommend()
            refreshList = { loadRecommend() }
        }

        fansText.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fansText.setTextAppearance(R.style.SelectedFocusListText)
                recommendText.setTextAppearance(R.style.FocusListText)
                focusText.setTextAppearance(R.style.FocusListText)
            }
            loadFans()

            refreshList = { loadFans() }
        }

        focusText.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                focusText.setTextAppearance(R.style.SelectedFocusListText)
                recommendText.setTextAppearance(R.style.FocusListText)
                fansText.setTextAppearance(R.style.FocusListText)
            }
            loadFocus()
            refreshList = { loadFocus() }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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

        listRefresh.setOnRefreshListener(this::refresh)

        activity?.hideSoftInputMethod()

        return view
    }

    private fun refresh() = refreshList()


    private fun loadRecommend() {
        restoreUI = { loadRecommend() }
        loadMoreList = { loadMoreRecommend() }

        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            itemManager.refreshAll {
            }
            val recommendUsers = AttentionService.getRecommendUser(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT)
                    .show()
                listRefresh.isRefreshing = false
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                recommendUsers.forEach { recommendUser ->
                    recommendItem(recommendUser, this@AttentionListFragment.context!!) {
                        val intent = Intent()
                        intent.putExtra("userID", recommendUser.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
            listRefresh.isRefreshing = false
        }
    }

    private fun loadMoreRecommend() {


        launch(UI + QuietCoroutineExceptionHandler) {
            val recommendUsers = AttentionService.getRecommendUser(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                recommendUsers.forEach { recommendUser ->
                    recommendItem(recommendUser, this@AttentionListFragment.context!!) {
                        val intent = Intent()
                        intent.putExtra("userID", recommendUser.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
        }
    }

    private fun loadFans() {
        restoreUI = { loadFans() }
        loadMoreList = { loadMoreFans() }

        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            itemManager.refreshAll {
            }
            val fans = AttentionService.getFans(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "粉丝加载失败", Toast.LENGTH_SHORT)
                    .show()
                listRefresh.isRefreshing = false
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                fans.data.forEach { fan ->
                    fansItem(fan, this@AttentionListFragment.context!!) {
                        val intent = Intent()
                        intent.putExtra("userID", fan.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
            listRefresh.isRefreshing = false
        }
    }

    private fun loadMoreFans() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val fans = AttentionService.getFans(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "粉丝加载失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                fans.data.forEach { fan ->
                    fansItem(fan, this@AttentionListFragment.context!!) {
                        val intent = Intent()
                        intent.putExtra("userID", fan.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
        }
    }

    private fun loadFocus() {
        restoreUI = { loadFocus() }
        loadMoreList = { loadMoreFocus() }

        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            itemManager.refreshAll {
            }
            val concernPersons = AttentionService.getSpotList(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT)
                    .show()
                listRefresh.isRefreshing = false
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                concernPersons.data?.forEach { concernPerson ->
                    focusItem(concernPerson, this@AttentionListFragment.context!!) {
                        val intent = Intent()
                        intent.putExtra("userID", concernPerson.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
            listRefresh.isRefreshing = false
        }
    }

    private fun loadMoreFocus() {


        launch(UI + QuietCoroutineExceptionHandler) {
            val concernPersons = AttentionService.getSpotList(page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "推荐加载失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                concernPersons.data?.forEach { concernPerson ->
                    focusItem(concernPerson, this@AttentionListFragment.context!!) {
                        val intent = Intent()
                        intent.putExtra("userID", concernPerson.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
        }
    }

    private fun loadSearch() {
        loadMoreList = { loadMoreSearch() }

        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            searchToolbar.visibility = View.GONE
            itemManager.refreshAll {
            }
            val result = AttentionService.search(page, searchEdit.text.toString()).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            lastPage = result.user.lastPage
            val usersOfResult = result.user.data
            itemManager.refreshAll {
                clear()
                usersOfResult?.forEach { dataOfUser ->
                    historyItem(dataOfUser, this@AttentionListFragment.context!!) {
                        AttentionUtils.setSearchHistory(dataOfUser)
                        val intent = Intent()
                        intent.putExtra("userID", dataOfUser.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
            listRefresh.isRefreshing = false
        }
    }

    private fun loadMoreSearch() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (page > lastPage) {
                Toast.makeText(this@AttentionListFragment.context, "没有更多了", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            val result = AttentionService.search(page, searchEdit.text.toString()).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@AttentionListFragment.context, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            lastPage = result.user.lastPage
            val usersOfResult = result.user.data
            itemManager.autoRefresh {
                usersOfResult?.forEach { dataOfUser ->
                    historyItem(dataOfUser, this@AttentionListFragment.context!!) {
                        AttentionUtils.setSearchHistory(dataOfUser)
                        val intent = Intent()
                        intent.putExtra("userID", dataOfUser.user_ID.toString())
                        Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                    }
                }
            }
            isLoading = false
            listRefresh.isRefreshing = false
        }
    }

    private fun showHistory() {
        searchToolbar.visibility = View.VISIBLE
        itemManager.refreshAll {
            clear()
            AttentionUtils.searchHistory.forEach { dataOfUser ->
                historyItem(dataOfUser, this@AttentionListFragment.context!!) {
                    AttentionUtils.setSearchHistory(dataOfUser)
                    val intent = Intent()
                    intent.putExtra("userID", dataOfUser.user_ID.toString())
                    Transfer.startActivityWithoutClose(activity!!, "MyselfActivity", intent)
                }
            }
        }

        listRefresh.isRefreshing = false
    }

    fun setView(live: TextView, back: ImageView) {
        liveLabel = live
        backLabel = back
    }

    fun refreshView() {
        restoreUI()
        searchEdit.clearFocus()
        searchEdit.text.clear()
        activity?.hideSoftInputMethod()
        tabs.visibility = View.VISIBLE
        searchToolbar.visibility = View.GONE
        liveLabel?.visibility = View.VISIBLE
        backLabel?.visibility = View.GONE
    }


}