package com.wingedvampires.attention.view

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.common.experimental.extensions.morowindow.MoreWindow
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator
import com.wingedvampires.attention.R

class AttentionActivity : AppCompatActivity() {
    private lateinit var image: ImageView
    private var mMoreWindow: MoreWindow? = null
    private lateinit var homepageViewPager: ViewPager
    private lateinit var dynamicPagerIndicator: DynamicPagerIndicator
    private lateinit var attentionViewPager: AttentionViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attention)
        image = findViewById(R.id.iv_attention_camera)
        image.setOnClickListener { v -> showMoreWindow(v) }
        val homepage = findViewById<TextView>(R.id.tv_bottom_homepage)
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_find)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)


        homepageViewPager = findViewById(R.id.vp_attention_main)
        dynamicPagerIndicator = findViewById(R.id.dynamic_pager_indicator2)
        setViewPagerContent()
    }

    //音乐、舞蹈、搞笑、颜值、小剧场和其他
    private fun setViewPagerContent() {
        attentionViewPager = AttentionViewPager(supportFragmentManager)
        attentionViewPager.apply {
            add("关注", AttentionFocusFragment())
            add("列表", AttentionListFragment())
        }
        homepageViewPager.adapter = attentionViewPager
        dynamicPagerIndicator.setViewPager(homepageViewPager)
    }

    private fun showMoreWindow(view: View) {
        if (mMoreWindow != null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view)
    }
}
