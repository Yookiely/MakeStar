package com.wingedvampires.homepage.view

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.extendion.MoreWindow

class HomePageActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private var mMoreWindow: MoreWindow? = null
    private lateinit var homepageViewPager: ViewPager
    private lateinit var dynamicPagerIndicator: DynamicPagerIndicator
    private lateinit var dynamicFragmentPagerAdapter: DynamicFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        image = findViewById(R.id.show)
        image.setOnClickListener { v -> showMoreWindow(v) }

        homepageViewPager = findViewById(R.id.view_pager2)
        dynamicPagerIndicator = findViewById(R.id.dynamic_pager_indicator2)
        setViewPagerContent()
    }

    private fun setViewPagerContent() {
        dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
//        for (i in 0..29) {
//            dynamicFragmentPagerAdapter.add(i.toString(), PagerFragment.create(i))
//        }
        homepageViewPager.adapter = dynamicFragmentPagerAdapter
        dynamicPagerIndicator.setViewPager(homepageViewPager)
    }

    private fun showMoreWindow(view: View) {
        if (mMoreWindow != null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view, 80)
    }

}
