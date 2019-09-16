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
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.view.items.commentItem
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex

class SecondCommentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val layoutManager = LinearLayoutManager(this)
    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var commentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_second_comment)
        val bundle: Bundle = intent.extras
        commentId = bundle.getString(AttentionUtils.SECOND_COMMENT_INDEX)!!
        val toolbar = findViewById<Toolbar>(R.id.tb_secondcomment_main)
        toolbar.apply {
            title = " "
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener { onBackPressed() }
        }

        recyclerView = findViewById(R.id.rv_secondcomment_main)
        recyclerView.apply {
            layoutManager = layoutManager
            adapter = ItemAdapter(itemManager)
        }
        loadSecondComment()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    page++

                    loadMoreSecondComment()
                }
            }
        })
        isLoading = false
    }

    private fun loadSecondComment() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 0

            val comments = AttentionService.getScecondComments(commentId, page).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@SecondCommentActivity, "评论加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.refreshAll {
                comments.data.forEachWithIndex { index, comment ->
                    commentItem(
                        this@SecondCommentActivity,
                        index != comments.data.size,
                        null,
                        comment,
                        false
                    ) {
                        this.remove(it)
                    }
                }
            }

            lastPage = comments.lastPage
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
                comments.data.forEachWithIndex { index, comment ->
                    commentItem(
                        this@SecondCommentActivity,
                        index != comments.data.size,
                        null,
                        comment,
                        false
                    ) {
                        this.remove(it)
                    }
                }
            }

            lastPage = comments.lastPage
        }
    }
}
