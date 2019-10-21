package com.yookie.discover

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.discover.view.ActivityFragment
import com.yookie.discover.view.DynamicFragmentPagerAdapter
import com.yookie.discover.view.RankFragment
import kotlinx.android.synthetic.main.activity_discover.*

class DiscoverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_discover)
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_homepage)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)
        attention.setOnClickListener { Transfer.startActivity(this, "AttentionActivity", Intent()) }
        discover.setOnClickListener { Transfer.startActivity(this, "HomePageActivity", Intent()) }
        userpage.setOnClickListener { Transfer.startActivity(this, "MyUserActivity", Intent()) }
        val dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        dynamicFragmentPagerAdapter.add("排行",RankFragment())
        dynamicFragmentPagerAdapter.add("活动",ActivityFragment())
        vp_homepage_main.adapter = dynamicFragmentPagerAdapter
        dynamic_pager_indicator2.setViewPager(vp_homepage_main)


    }
}