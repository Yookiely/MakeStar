package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.yangzihang.makestar.View.MessageFragment
import com.example.yangzihang.makestar.View.MessagePagerAdapter
import q.rorbin.badgeview.QBadgeView

class MessageActivity : AppCompatActivity() {

    lateinit var mTabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        mTabLayout = findViewById(R.id.message_home_table)
        val viewPager = findViewById<ViewPager>(R.id.message_home_viewpager)
        val mPagerAdapter = MessagePagerAdapter(supportFragmentManager)
        mPagerAdapter.apply {
            add(MessageFragment(),"消息")
            add(MessageFragment(),"2")
            add(MessageFragment(),"3")
        }
        viewPager.adapter = mPagerAdapter
        mTabLayout.setupWithViewPager(viewPager)
        setTabBadage(0,10,"消息")
        setTabBadage(1,9,"点赞")
        setTabBadage(2,1,"评论")


    }

    fun setTabBadage(index: Int,num : Int,title : String) {
        val mtab = mTabLayout.getTabAt(index)
        val inflater = View.inflate(this, R.layout.item_tablayout_text, null)
        val texView = inflater.findViewById<TextView>(R.id.tv)
        texView.text = title
        mtab?.customView = inflater
        val badage = QBadgeView(this).bindTarget(texView)
        badage.badgeNumber = num
        badage.setBadgeTextSize(12f,true)
        badage.badgeGravity = Gravity.END or Gravity.TOP

    }
}
