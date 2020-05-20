package com.yookie.discover

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.common.experimental.extensions.morewindow.MoreWindow
import com.yookie.discover.view.DynamicFragmentPagerAdapter
import com.yookie.discover.view.RankFragment
import kotlinx.android.synthetic.main.activity_discover.*

class DiscoverActivity : AppCompatActivity() {

    lateinit var image: ImageView
    private var mMoreWindow: MoreWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_discover)
        image = findViewById(R.id.iv_discover_camera)
        image.setOnClickListener { v -> showMoreWindow(v) }
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_homepage)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)
        attention.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "AttentionActivity", Intent())
        }
        discover.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "HomePageActivity", Intent())
        }
        userpage.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "MyUserActivity", Intent())
        }
        val localLayoutParams = window.attributes
        localLayoutParams.flags =
            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        val dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        dynamicFragmentPagerAdapter.add("排行榜", RankFragment())
//        dynamicFragmentPagerAdapter.add("活动",ActivityFragment())
        vp_homepage_main.adapter = dynamicFragmentPagerAdapter
        dynamic_pager_indicator2.setViewPager(vp_homepage_main)


    }


    private fun showMoreWindow(view: View) {
        if (mMoreWindow == null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view)
    }
}
