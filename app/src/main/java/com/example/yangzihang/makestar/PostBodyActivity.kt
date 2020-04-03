package com.example.yangzihang.makestar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.View.FansCommentItem
import com.example.yangzihang.makestar.network.UserImp
import com.example.yangzihang.makestar.network.UserService
import com.hb.dialog.dialog.LoadingDialog
import com.wingedvampires.attention.model.AttentionService
import com.yookie.common.experimental.extensions.ComplaintType
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_post_body.*
import kotlinx.android.synthetic.main.fans_comment_edit.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class PostBodyActivity : AppCompatActivity() {
    private var itemManager: ItemManager =
        ItemManager() //by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    lateinit var fansCommentRefresh: SwipeRefreshLayout
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var fandomId :String
    lateinit var rec :RecyclerView
    lateinit var complain : ImageView
    lateinit var complainText :TextView
    private lateinit var loadingDialog: LoadingDialog
    var items = mutableListOf<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_post_body)
        rec=findViewById(R.id.fans_post_rec)
        fansCommentRefresh = findViewById(R.id.fans_post_refresh)
        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在上传")
        val name = intent.getStringExtra("name")
        val avater = intent.getStringExtra("avatar")
        val time = intent.getStringExtra("time")
        val text =intent.getStringExtra("text")
        fandomId =intent.getStringExtra("fandomid")
        Log.d("fandomid",fandomId)
        val mLayoutManager = LinearLayoutManager(this)
        val imgs =intent.getStringArrayListExtra("imgs")
        val localLayoutParams = window.attributes
        complain =findViewById(R.id.fans_post_complain)
        complainText =findViewById(R.id.fans_text_post_complain)
        fansCommentRefresh.setOnRefreshListener(this::loadMainComment)
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        rec.layoutManager =mLayoutManager
        complainText.setOnClickListener {
            val intent = Intent(this@PostBodyActivity, ComplaintActivity::class.java)
            intent.putExtra(ComplaintType.COMPLAINT_TYPE, ComplaintType.LINE)
            intent.putExtra(
                ComplaintType.COMPLAINT_ID,
                fandomId
            )
            startActivity(intent)
        }
        complain.setOnClickListener {
            val intent = Intent(this@PostBodyActivity, ComplaintActivity::class.java)
            intent.putExtra(ComplaintType.COMPLAINT_TYPE, ComplaintType.LINE)
            intent.putExtra(
                ComplaintType.COMPLAINT_ID,
                fandomId
            )
            startActivity(intent)
        }
        iv_post_back.setOnClickListener {
            onBackPressed()
        }
        Glide.with(this)
            .load(avater)
            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
            .into(fans_post_portrait)
        fans_post_name.text = name
        fans_post_time.text =time
        fans_post_text.text =text
        when (imgs.size) {
            0 -> fans_post_photo.visibility = View.INVISIBLE
            1 -> {
                fans_post_photo.layoutParams.height= 480
                fans_post_photo.visibility = View.VISIBLE
                Glide.with(this)
                    .load(imgs[0])
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(fans_post_photo)
            }
            2 -> {
                Glide.with(this)
                    .load(imgs[0])
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(fans_post_dou_photo)
                Glide.with(this)
                    .load(imgs[1])
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(fans_post_dou_photo2)
            }
            in 3..9 -> {
                Glide.with(this)
                    .load(imgs[0])
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(fans_post_tri_photo)
                Glide.with(this)
                    .load(imgs[1])
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(fans_post_tri_photo2)
                Glide.with(this)
                    .load(imgs[2])
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(fans_post_tri_photo3)
                if(imgs.size >=4){
                    Glide.with(this)
                        .load(imgs[3])
                        .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                        .into(fans_post_tri2_photo)
                }
                if(imgs.size >=5){
                    Glide.with(this)
                        .load(imgs[4])
                        .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                        .into(fans_post_tri2_photo2)
                }
                if(imgs.size >=6){
                    Glide.with(this)
                        .load(imgs[5])
                        .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                        .into(fans_post_tri2_photo3)
                }
                if(imgs.size >=7){
                    Glide.with(this)
                        .load(imgs[6])
                        .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                        .into(fans_post_tri3_photo)
                }
                if(imgs.size >=8){
                    Glide.with(this)
                        .load(imgs[7])
                        .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                        .into(fans_post_tri3_photo2)
                }
                if(imgs.size >=9){
                    Glide.with(this)
                        .load(imgs[8])
                        .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                        .into(fans_post_tri3_photo3)
                }
            }
        }
        loadMainComment()
        rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && (lastVisibleItem + 2 == totalCount)) { //还有一点问题
                    isLoading = true
                    page++
                    loadMoreMainComment()
                }
            }
        })
        fans_comment_confirm.setOnClickListener{
            if (fans_comment_input.text.isNotBlank()) {
                fans_comment_input.clearFocus()
                hideSoftInputMethod()
                sendMainComment()
            }
        }
        fans_comment_input.apply {
            isFocusable = false
            isFocusableInTouchMode = true
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND && fans_comment_input.text.isNotBlank()) {
                    this.clearFocus()
                    hideSoftInputMethod()
                    sendMainComment()
                }

                true
            }
        }
    }
    private fun loadMainComment(){
        page=1
        items .clear()
        UserImp.getCommemtByActionID(fandomId,20,page){ //先用着假数据
            it.data.forEach {fansCommentText->
                items.add(FansCommentItem(this,fansCommentText,null))
                lastPage = it.last_page
            }
            rec.withItems(items)
            lastPage = it.last_page
        }
        fansCommentRefresh.isRefreshing = false
        isLoading = false
    }

    private fun loadMoreMainComment(){
        page++
        if(page>lastPage){
            Toast.makeText(this,"已经到底了！",Toast.LENGTH_SHORT).show()
        }
        UserImp.getCommemtByActionID(fandomId,20,page){
            lastPage = it.last_page
            it.data.forEach {fansCommentText->
                items.add(FansCommentItem(this,fansCommentText,null))

            }
            rec.withItems(items)
        }
        isLoading = false
    }
    private fun hideSoftInputMethod() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.apply {
            hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }
    private fun sendMainComment() {
        loadingDialog.show()
        launch(UI + QuietCoroutineExceptionHandler) {
            val result = UserService.sendActionComment(
                CommonPreferences.userid,
                fans_comment_input.text.toString(),
                CommonPreferences.userid,
                fandomId
            ).awaitAndHandle {
                it.printStackTrace()
                loadingDialog.dismiss()
                Toast.makeText(this@PostBodyActivity, "发送失败", Toast.LENGTH_SHORT).show()
            }

            if (result == null || result.error_code != -1) {
                Toast.makeText(this@PostBodyActivity, "发送失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                return@launch
            } else {
                Toast.makeText(this@PostBodyActivity, "发送成功", Toast.LENGTH_SHORT).show()
                fans_comment_input.setText("")
                loadingDialog.dismiss()
                loadMainComment()
            }
        }
    }
}
