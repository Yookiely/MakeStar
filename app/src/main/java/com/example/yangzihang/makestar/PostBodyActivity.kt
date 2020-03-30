package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.View.FansCommentItem
import com.example.yangzihang.makestar.network.FansCommentText
import com.example.yangzihang.makestar.network.UserImp
import kotlinx.android.synthetic.main.activity_post_body.*

class PostBodyActivity : AppCompatActivity() {
    private var itemManager: ItemManager =
        ItemManager() //by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    lateinit var fansCommentRefresh: SwipeRefreshLayout
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    private var fandomId :Int =0
    lateinit var rec :RecyclerView
    var items = mutableListOf<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_post_body)
        rec=findViewById(R.id.fans_post_rec)
        fansCommentRefresh = findViewById(R.id.fans_post_refresh)
        val name = intent.getStringExtra("name")
        val avater = intent.getStringExtra("avatar")
        val time = intent.getStringExtra("time")
        val text =intent.getStringExtra("text")
        fandomId =intent.getIntExtra("fandomid",0)
        val mLayoutManager = LinearLayoutManager(this)
        val imgs =intent.getStringArrayListExtra("imgs")
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        rec.layoutManager =mLayoutManager
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
    }
    private fun loadMainComment(){
        page=1
        UserImp.getCommemtByActionID(1,10,page){ //先用着假数据
            it.data.forEach {fansCommentText->
                items.add(FansCommentItem(this,fansCommentText,null))
                lastPage = it.last_page
            }
            rec.withItems(items)
        }
        fansCommentRefresh.isRefreshing = false
        isLoading = false
    }
    private fun loadMoreMainComment(){
        if(page>lastPage){
            Toast.makeText(this,"已经到底了！",Toast.LENGTH_SHORT).show()
        }
        UserImp.getCommemtByActionID(1,10,page){
            it.data.forEach {fansCommentText->
                items.add(FansCommentItem(this,fansCommentText,null))
                lastPage = it.last_page
            }
            rec.withItems(items)
        }
        isLoading = false
    }

}
