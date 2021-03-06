package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.example.yangzihang.makestar.View.AccumulationFragment
import com.example.yangzihang.makestar.View.RankHistoryFragment
import com.yookie.discover.view.DynamicFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_rankhistory.*


class RankhistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_rankhistory)
        val dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        dynamicFragmentPagerAdapter.add("历史排名", RankHistoryFragment())
        dynamicFragmentPagerAdapter.add("人气积累", AccumulationFragment())
        vp_user_rankhistory.adapter = dynamicFragmentPagerAdapter
        dynamic_pager_indicator2_rankhistory.setViewPager(vp_user_rankhistory)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
    }
}
