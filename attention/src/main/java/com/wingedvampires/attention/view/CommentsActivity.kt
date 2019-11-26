package com.wingedvampires.attention.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Window
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.view.items.commentItem
import com.wingedvampires.attention.view.items.videoActionCommentItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex

class CommentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager =
        ItemManager() //by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var workId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_comments)
        val bundle: Bundle = intent.extras
        workId = bundle.getString(AttentionUtils.COMMENT_INDEX)!!
        val toolbar = findViewById<Toolbar>(R.id.tb_comment_main)
        val mLayoutManager = LinearLayoutManager(this)
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

        loadMainComment()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
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
            val work = AttentionService.getWorkByID(workId).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@CommentsActivity, "数据加载失败", Toast.LENGTH_SHORT).show()
            }?.data?.get(0) ?: return@launch
            val comments = AttentionService.getTotalComments(workId, page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@CommentsActivity, "评论加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                videoActionCommentItem(work, this@CommentsActivity)
                comments.data?.forEachWithIndex { index, comment ->
                    commentItem(this@CommentsActivity, index != comments.data?.size, comment) {
                        this.remove(it)
                    }
                }
            }

            lastPage = comments.lastPage
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
                        index != comments.data.size,
                        comment
                    ) {
                        this.remove(it)
                    }
                }
            }

            lastPage = comments.lastPage
            isLoading = false
        }
    }
}
