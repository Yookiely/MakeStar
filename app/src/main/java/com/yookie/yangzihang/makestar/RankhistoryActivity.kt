package com.yookie.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.yookie.discover.view.ActivityFragment
import com.yookie.discover.view.DynamicFragmentPagerAdapter
import com.yookie.discover.view.RankFragment
import com.yookie.yangzihang.makestar.View.AccumulationFragment
import com.yookie.yangzihang.makestar.View.RankHistoryFragment
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
    }
}
