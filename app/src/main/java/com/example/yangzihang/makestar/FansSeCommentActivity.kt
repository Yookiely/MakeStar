package com.example.yangzihang.makestar

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.View.FansCommentItem
import com.example.yangzihang.makestar.network.UserImp
import com.example.yangzihang.makestar.network.UserService
import com.hb.dialog.dialog.LoadingDialog
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_fans_second_comment.*
import kotlinx.android.synthetic.main.fans_comment_edit.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class FansSeCommentActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var secondCommitRefresh: SwipeRefreshLayout
    lateinit var commentId: String
    private var facId = 0
    private lateinit var loadingDialog: LoadingDialog
    var items = mutableListOf<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fans_second_comment)
        secondCommitRefresh=findViewById(R.id.fans_secondcomment_refresh)
        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在上传")
        loadingDialog.setCanceledOnTouchOutside(false)
        facId =intent.getIntExtra("facid",0)
        commentId = facId.toString()
        val mLayoutManager = LinearLayoutManager(this)
        fans_secondcomment_back.setOnClickListener {
            onBackPressed()
        }
        recyclerView=findViewById(R.id.fans_secondcomment_rec)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        loadSeComment()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    loadMoreSecondComment()
                }
            }
        })
        fans_comment_confirm.setOnClickListener{
            if (fans_comment_input.text.isNotBlank()) {
                fans_comment_input.clearFocus()
                hideSoftInputMethod()
                sendSecondComment()
            }
        }
        fans_comment_input.apply {
            isFocusable = false
            isFocusableInTouchMode = true
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND && fans_comment_input.text.isNotBlank()) {
                    this.clearFocus()
                    hideSoftInputMethod()
                    sendSecondComment()
                }

                true
            }
        }
        secondCommitRefresh.setOnRefreshListener(this::loadSeComment)
    }
    private fun loadSeComment(){
        page= 1
        items.clear()
        UserImp.getCommentCommentByAcID(facId,99,page){ //假数据
            it.data.forEach {fansSecomment->
                items.add(FansCommentItem(this,null,fansSecomment))
            }
            lastPage = it.last_page
            recyclerView.withItems(items)
            secondCommitRefresh.isRefreshing = false
            isLoading = false
        }

    }
    private fun loadMoreSecondComment() {
        page++
        if(page>lastPage){
            Toast.makeText(this,"已经到底了！",Toast.LENGTH_SHORT).show()
        }
        UserImp.getCommentCommentByAcID(facId,99,page){
            itemManager.refreshAll {
                it.data.forEach {fansComment->
                    FansCommentItem(this@FansSeCommentActivity,null,fansComment)
                }
                lastPage =it.last_page
            }
            secondCommitRefresh.isRefreshing = false
            isLoading = false
        }

    }
    private fun hideSoftInputMethod() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.apply {
            hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }
    private fun sendSecondComment() {
        loadingDialog.show()
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = UserService.sendActionCommentC(
                CommonPreferences.userid,
                fans_comment_input.text.toString(),
                CommonPreferences.userid,
                commentId
            ).awaitAndHandle {
                it.printStackTrace()
                loadingDialog.dismiss()
                Toast.makeText(this@FansSeCommentActivity, "发送失败", Toast.LENGTH_SHORT)
                    .show()
            }

            if (result == null || result.error_code != -1) {
                Toast.makeText(this@FansSeCommentActivity, "发送失败", Toast.LENGTH_SHORT)
                    .show()
                loadingDialog.dismiss()
                return@launch
            } else {
                Toast.makeText(this@FansSeCommentActivity, "发送成功", Toast.LENGTH_SHORT)
                    .show()
                fans_comment_input.setText("")
                loadingDialog.dismiss()
                loadSeComment()
            }
        }
    }
}
