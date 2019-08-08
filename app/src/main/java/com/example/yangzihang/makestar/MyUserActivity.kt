package com.example.yangzihang.makestar

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.bumptech.glide.Glide
import com.example.auth.api.authSelfLiveData
import com.example.common.experimental.extensions.bindNonNull
import com.example.yangzihang.makestar.View.setUserText

class MyUserActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_my_user)
        var portrait = findViewById<ImageView>(R.id.portrait)
        var rank = findViewById<TextView>(R.id.rank)
        var nickname = findViewById<TextView>(R.id.nickname)
        var message = findViewById<TextView>(R.id.message)
        var userId = findViewById<TextView>(R.id.user_id)
        var mystyle = findViewById<TextView>(R.id.style)
        var fansNum = findViewById<TextView>(R.id.people_num)
        var popNum = findViewById<TextView>(R.id.pop_num)
        var focusNum = findViewById<TextView>(R.id.focus_num)
        recyclerView = findViewById(R.id.user_rec)

        authSelfLiveData.bindNonNull(this) {
            Glide.with(this)
                .load(it.data?.avatar)
                .into(portrait)
            it.data?.apply {
                fansNum.text = this.fans_num
                mystyle.text = this.signature
                nickname.text = this.username
                userId.text = this.user_ID
                popNum.text = this.year_hot_value
                focusNum.text = this.spotted
                message.text = "♀" + " | " + this.age + " | " + this.city
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.withItems {
            setUserText("历史排名")
            setUserText("我的消息")
            setUserText("我的收藏")
            setUserText("我的作品")
            setUserText("我的订单")
            setUserText("商务合作")
        }
    }
}
