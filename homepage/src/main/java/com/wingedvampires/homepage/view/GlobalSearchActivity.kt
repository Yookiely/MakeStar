package com.wingedvampires.homepage.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageService
import com.wingedvampires.homepage.model.HomePageUtils
import com.wingedvampires.homepage.view.items.searchTitleItem
import com.wingedvampires.homepage.view.items.searchUserItem
import com.wingedvampires.homepage.view.items.searchVideoItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.android.synthetic.main.activity_global_search.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.startActivity

class GlobalSearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    private lateinit var searchEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_global_search)
        val mLayoutManager = LinearLayoutManager(this)
        searchEdit = findViewById(R.id.et_homepage_search)
        recyclerView = findViewById(R.id.rv_search_main)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }

        iv_homepage_search_back.setOnClickListener {
            onBackPressed()
        }

        searchEdit.clearFocus()
        searchEdit.apply {
            isFocusable = false
            isFocusableInTouchMode = true
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && searchEdit.text.isNotBlank()) {
                    loadSearch()
                    searchEdit.isFocusable = false
                    searchEdit.isFocusableInTouchMode = true
                    hideSoftInputMethod()
                }

                true
            }
        }
        if (intent.getStringExtra("SearchText")!=null){
            searchEdit.setText(intent.getStringExtra("SearchText"))
            loadSearch()
            searchEdit.isFocusable = false
            searchEdit.isFocusableInTouchMode = true
            hideSoftInputMethod()
        }

        hideSoftInputMethod()
    }

    private fun loadSearch() {
        launch(UI + QuietCoroutineExceptionHandler) {
            itemManager.refreshAll {
            }

            val result = HomePageService.search(1, searchEdit.text.toString()).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@GlobalSearchActivity, "搜索失败", Toast.LENGTH_SHORT)
                    .show()
            }?.data ?: return@launch

            val users = result.user.data
            val works = result.work.data
            itemManager.refreshAll {
                clear()
                searchTitleItem("用户") {
                    it.context.startActivity<MoreSearchActivity>(
                        HomePageUtils.SEARCH_TYPE to HomePageUtils.SEARCH_USER,
                        HomePageUtils.SEARCH_CONTENT to searchEdit.text.toString()
                    )
                }
                users?.forEachWithIndex { i, dataOfUser ->
                    if (i <= 2) {
                        searchUserItem(dataOfUser) {
                            val intent = Intent()
                            intent.putExtra("userID", dataOfUser.user_ID)
                            Transfer.startActivityWithoutClose(
                                this@GlobalSearchActivity,
                                "MyselfActivity",
                                intent
                            )
                        }
                    }
                }
                searchTitleItem("视频") {
                    it.context.startActivity<MoreSearchActivity>(
                        HomePageUtils.SEARCH_TYPE to HomePageUtils.SEARCH_VIDEO,
                        HomePageUtils.SEARCH_CONTENT to searchEdit.text.toString()
                    )
                }
                works?.forEachWithIndex { i, dataOfWork ->
                    if (i <= 1) {
                        searchVideoItem(dataOfWork) {
                            val intent = Intent().also {
                                it.putExtra(
                                    HomePageUtils.VIDEO_PALY_WORKID,
                                    dataOfWork.work_ID
                                )
                            }

                            Transfer.startActivityWithoutClose(
                                this@GlobalSearchActivity,
                                "VideoPlayActivity",
                                intent
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hideSoftInputMethod() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.apply {
            hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }
}
