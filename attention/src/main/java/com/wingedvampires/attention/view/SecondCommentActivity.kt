package com.wingedvampires.attention.view

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.view.items.commentItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_second_comment.*
import kotlinx.android.synthetic.main.component_comment_edit.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex

class SecondCommentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var secondCommitRefresh: SwipeRefreshLayout
    lateinit var commentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_second_comment)
        val bundle: Bundle = intent.extras
        commentId = bundle.getString(AttentionUtils.SECOND_COMMENT_INDEX)!!
        val toolbar = findViewById<Toolbar>(R.id.tb_secondcomment_main)
        val mLayoutManager = LinearLayoutManager(this)
        secondCommitRefresh = findViewById(R.id.sl_secondcommit_main)
        toolbar.apply {
            title = " "
            setSupportActionBar(this)
        }

        iv_secondcomment_back.setOnClickListener { onBackPressed() }
        recyclerView = findViewById(R.id.rv_secondcomment_main)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }
        loadSecondComment()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++

                    loadMoreSecondComment()
                }
            }
        })

        et_comment_input.apply {
            isFocusable = false
            isFocusableInTouchMode = true
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND && et_comment_input.text.isNotBlank()) {
                    this.clearFocus()
                    hideSoftInputMethod()
                    sendSecondComment()
                }

                true
            }
        }

        et_comment_confirm.setOnClickListener {
            if (et_comment_input.text.isNotBlank()) {
                et_comment_input.clearFocus()
                hideSoftInputMethod()
                sendSecondComment()
            }
        }

        secondCommitRefresh.setOnRefreshListener(this::loadSecondComment)

    }

    private fun loadSecondComment() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 0

            val comments = AttentionService.getScecondComments(commentId, page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@SecondCommentActivity, "评论加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                comments.data?.forEachWithIndex { index, comment ->
                    commentItem(
                        this@SecondCommentActivity,
                        this@SecondCommentActivity,
                        index != comments.data.size,
                        null,
                        comment,
                        false
                    ) {
                        loadSecondComment()
                    }
                }
            }

            lastPage = comments.lastPage
            isLoading = false
            secondCommitRefresh.isRefreshing = false
        }
    }

    private fun loadMoreSecondComment() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (page > lastPage) {
                Toast.makeText(this@SecondCommentActivity, "已经到底了", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val comments = AttentionService.getScecondComments(commentId, page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@SecondCommentActivity, "评论加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                comments.data?.forEachWithIndex { index, comment ->
                    commentItem(
                        this@SecondCommentActivity,
                        this@SecondCommentActivity,
                        index != comments.data.size,
                        null,
                        comment,
                        false
                    ) {
                        loadSecondComment()
                    }
                }
            }

            lastPage = comments.lastPage
            isLoading = false
        }
    }

    private fun sendSecondComment() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = AttentionService.createSecondComment(
                commentId,
                et_comment_input.text.toString(),
                CommonPreferences.userid
            ).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@SecondCommentActivity, "发送失败", Toast.LENGTH_SHORT)
                    .show()
            }

            if (result == null || result.error_code != -1) {
                Toast.makeText(this@SecondCommentActivity, "发送失败", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            } else {
                Toast.makeText(this@SecondCommentActivity, "发送成功", Toast.LENGTH_SHORT)
                    .show()
                et_comment_input.setText("")
                loadSecondComment()
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
