package com.example.yangzihang.makestar

import android.annotation.SuppressLint
import android.content.Intent
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
        val portrait = findViewById<ImageView>(R.id.portrait)
        var rank = findViewById<TextView>(R.id.rank)
        val nickname = findViewById<TextView>(R.id.nickname)
        val message = findViewById<TextView>(R.id.message)
        val userId = findViewById<TextView>(R.id.user_id)
        val mystyle = findViewById<TextView>(R.id.style)
        val fansNum = findViewById<TextView>(R.id.people_num)
        val popNum = findViewById<TextView>(R.id.pop_num)
        val focusNum = findViewById<TextView>(R.id.focus_num)
        val enter = findViewById<TextView>(R.id.enter_oneself)

        enter.setOnClickListener {
            startActivity(Intent(this,MyselfActivity::class.java))
        }

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
            setUserText("历史排名",this@MyUserActivity,1)
            setUserText("我的消息",this@MyUserActivity,2)
            setUserText("我的收藏",this@MyUserActivity,3)
            setUserText("粉丝圈",this@MyUserActivity,4)
            setUserText("我的订单",this@MyUserActivity,5)
            setUserText("商务合作",this@MyUserActivity,6)
        }
    }
}
