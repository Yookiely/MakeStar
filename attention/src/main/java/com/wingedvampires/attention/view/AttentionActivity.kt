package com.wingedvampires.attention.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.common.experimental.extensions.jumpchannel.Transfer
import com.example.common.experimental.extensions.morewindow.MoreWindow
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator
import com.wingedvampires.attention.R

class AttentionActivity : AppCompatActivity() {
    private lateinit var image: ImageView
    private var mMoreWindow: MoreWindow? = null
    private lateinit var homepageViewPager: ViewPager
    private lateinit var dynamicPagerIndicator: DynamicPagerIndicator
    private lateinit var attentionViewPager: AttentionViewPager
    private lateinit var liveLabel: TextView
    private lateinit var backLabel: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attention)
        image = findViewById(R.id.iv_attention_camera)
        image.setOnClickListener { v -> showMoreWindow(v) }
        liveLabel = findViewById(R.id.tv_attention_live_label)
        backLabel = findViewById(R.id.iv_attention_back)
        val homepage = findViewById<TextView>(R.id.tv_bottom_homepage)
//        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_find)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)
        homepage.setOnClickListener { Transfer.startActivity(this, "HomePageActivity", Intent()) }
//        attention.setOnClickListener { Transfer.startActivity(this, "AttentionActivity", Intent()) }
        discover.setOnClickListener { Transfer.startActivity(this, "DiscoverActivity", Intent()) }
        userpage.setOnClickListener { Transfer.startActivity(this, "MyUserActivity", Intent()) }

        homepageViewPager = findViewById(R.id.vp_attention_main)
        dynamicPagerIndicator = findViewById(R.id.dynamic_pager_indicator2)
        setViewPagerContent()
    }

    //音乐、舞蹈、搞笑、颜值、小剧场和其他
    private fun setViewPagerContent() {
        attentionViewPager = AttentionViewPager(supportFragmentManager)
        val attentionListFragment = AttentionListFragment()
        attentionListFragment.setView(liveLabel, backLabel)

        attentionViewPager.apply {

            add("关注", AttentionFocusFragment())
            add("列表", attentionListFragment)
        }
        homepageViewPager.adapter = attentionViewPager
        dynamicPagerIndicator.setViewPager(homepageViewPager)

        homepageViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    attentionListFragment.refreshView()
                }
            }

        })
    }

    private fun showMoreWindow(view: View) {
        if (mMoreWindow != null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view)
    }
}
