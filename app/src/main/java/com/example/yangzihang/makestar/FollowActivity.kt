package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.userpage.view.setUserText

class FollowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_follow)
        val recyclerView  = findViewById<RecyclerView>(R.id.user_rec)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.withItems{
            setUserText("历史排名")
            setUserText("我的消息")
            setUserText("我的收藏")
            setUserText("我的作品")
            setUserText("我的订单")
            setUserText("商务合作")
        }
    }
}
