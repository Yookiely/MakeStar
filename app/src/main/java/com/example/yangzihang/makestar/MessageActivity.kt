package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.example.yangzihang.makestar.View.DiscussFragment
import com.example.yangzihang.makestar.View.LikeFragment
import com.example.yangzihang.makestar.View.MessageFragment
import com.example.yangzihang.makestar.View.MessagePagerAdapter
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences
import q.rorbin.badgeview.QBadgeView

class MessageActivity : AppCompatActivity() {

    lateinit var mTabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_message)

        mTabLayout = findViewById(R.id.message_home_table)
        val viewPager = findViewById<ViewPager>(R.id.message_home_viewpager)
        val mPagerAdapter = MessagePagerAdapter(supportFragmentManager)
        mPagerAdapter.apply {
            add(MessageFragment(),"消息")
            add(LikeFragment(),"点赞")
            add(DiscussFragment(),"评论")
        }
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        viewPager.adapter = mPagerAdapter
        mTabLayout.setupWithViewPager(viewPager)
        CommonPreferences.newMessage = 0
            if (CommonPreferences.newUserMessage>0){
                setTabBadage(0,CommonPreferences.newUserMessage,"消息")
                CommonPreferences.newUserMessage = 0

            }
            if (CommonPreferences.newStarMessage>0){
                setTabBadage(1,CommonPreferences.newStarMessage,"点赞")
                CommonPreferences.newStarMessage = 0
            }
            if (CommonPreferences.newCommentMessage>0){
                setTabBadage(2,CommonPreferences.newCommentMessage,"评论")
                CommonPreferences.newCommentMessage = 0
            }



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
        badage.setOnDragStateChangedListener { _, _, _ -> }

    }
}
