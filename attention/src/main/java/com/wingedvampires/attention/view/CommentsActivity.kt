package com.wingedvampires.attention.view

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.hb.dialog.dialog.LoadingDialog
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.view.items.commentItem
import com.wingedvampires.attention.view.items.videoActionCommentItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.component_comment_edit.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex

class CommentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager =
        ItemManager() //by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    lateinit var commitRefresh: SwipeRefreshLayout
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var workId: String
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_comments)
        val bundle: Bundle = intent.extras
        workId = bundle.getString(AttentionUtils.COMMENT_INDEX)!!
        val toolbar = findViewById<Toolbar>(R.id.tb_comment_main)
        val localLayoutParams = window.attributes
        localLayoutParams.flags =
            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        val mLayoutManager = LinearLayoutManager(this)
        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在上传")
        commitRefresh = findViewById(R.id.sl_commit_main)
        toolbar.apply {
            title = ""
            setSupportActionBar(this)
        }
        iv_comment_back.setOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.rv_comment_main)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }
        commitRefresh.setOnRefreshListener(this::loadMainComment)
        et_comment_input.apply {
            isFocusable = false
            isFocusableInTouchMode = true
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND && et_comment_input.text.isNotBlank()) {
                    this.clearFocus()
                    hideSoftInputMethod()
                    sendMainComment()
                }

                true
            }
        }

        et_comment_confirm.setOnClickListener {
            if (et_comment_input.text.isNotBlank()) {
                et_comment_input.clearFocus()
                hideSoftInputMethod()
                sendMainComment()
            }
        }
        loadMainComment()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++

                    loadMoreMainComment()
                }
            }
        })

    }

    private fun loadMainComment() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            val workOrNot = AttentionService.getWorkByID(workId).awaitAndHandle {
                it.printStackTrace()
                commitRefresh.isRefreshing = false
                Toast.makeText(this@CommentsActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
            } ?: return@launch
            if (workOrNot.error_code != -1) {
                Toast.makeText(this@CommentsActivity, workOrNot.message, Toast.LENGTH_SHORT).show()
                onBackPressed()
                return@launch
            }
            val work = workOrNot.data?.get(0) ?: return@launch
            val comments = AttentionService.getTotalComments(workId, page).awaitAndHandle {
                it.printStackTrace()
                commitRefresh.isRefreshing = false
                Toast.makeText(this@CommentsActivity, "评论加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                videoActionCommentItem(this@CommentsActivity, work, this@CommentsActivity)
                comments.data?.forEachWithIndex { index, comment ->
                    commentItem(
                        this@CommentsActivity,
                        this@CommentsActivity,
                        index != comments.data.size,
                        comment
                    ) {
                        loadMainComment()
                    }
                }
            }

            lastPage = comments.lastPage
            commitRefresh.isRefreshing = false
            isLoading = false
        }
    }

    private fun loadMoreMainComment() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (page > lastPage) {
                Toast.makeText(this@CommentsActivity, "已经到底了", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val comments = AttentionService.getTotalComments(workId, page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@CommentsActivity, "评论加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                comments.data?.forEachWithIndex { index, comment ->
                    commentItem(
                        this@CommentsActivity,
                        this@CommentsActivity,
                        index != comments.data.size,
                        comment
                    ) {
                        loadMainComment()
                    }
                }
            }

            lastPage = comments.lastPage
            isLoading = false
            commitRefresh.isRefreshing = false
        }
    }

    private fun sendMainComment() {
        loadingDialog.show()
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = AttentionService.createComment(
                workId,
                et_comment_input.text.toString(),
                CommonPreferences.userid
            ).awaitAndHandle {
                it.printStackTrace()
                loadingDialog.dismiss()
                Toast.makeText(this@CommentsActivity, "发送失败", Toast.LENGTH_SHORT).show()
            }

            if (result == null || result.error_code != -1) {
                Toast.makeText(this@CommentsActivity, "发送失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                return@launch
            } else {
                Toast.makeText(this@CommentsActivity, "发送成功", Toast.LENGTH_SHORT).show()
                et_comment_input.setText("")
                loadingDialog.dismiss()
                loadMainComment()
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
