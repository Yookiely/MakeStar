package com.wingedvampires.homepage.view

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.example.common.experimental.extensions.awaitAndHandle
import com.example.common.experimental.extensions.morowindow.MoreWindow
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageService
import com.wingedvampires.homepage.model.HomePageUtils
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class HomePageActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private var mMoreWindow: MoreWindow? = null
    private lateinit var homepageViewPager: ViewPager
    private lateinit var dynamicPagerIndicator: DynamicPagerIndicator
    private lateinit var dynamicFragmentPagerAdapter: DynamicFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        image = findViewById(R.id.iv_homepage_camera)
        image.setOnClickListener { v -> showMoreWindow(v) }
        val homepage = findViewById<TextView>(R.id.tv_bottom_homepage)
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_find)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)

        homepageViewPager = findViewById(R.id.vp_homepage_main)
        dynamicPagerIndicator = findViewById(R.id.dynamic_pager_indicator2)
        setViewPagerContent()
    }


    private fun setViewPagerContent() {
        launch(UI + QuietCoroutineExceptionHandler) {
            dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
            //先添加推荐页面
            dynamicFragmentPagerAdapter.add(
                "推荐",
                HomePageFragment.newInstance(HomePageUtils.WORK_TYPE_ID_OF_RECOMMEND)
            )

            //音乐、舞蹈、搞笑、颜值、小剧场和其他
            val workTpyes = HomePageService.getWorkTypes().awaitAndHandle {
                Toast.makeText(this@HomePageActivity, "加载失败", Toast.LENGTH_SHORT).show()
            }?.let {
                if (it.error_code == -1) {
                    it.data?.forEach { workType ->
                        dynamicFragmentPagerAdapter.add(
                            workType.work_type_name,
                            HomePageFragment.newInstance(workType.work_type_ID)
                        )
                    }
                }

            }
            homepageViewPager.adapter = dynamicFragmentPagerAdapter
            dynamicPagerIndicator.setViewPager(homepageViewPager)
        }


    }

    private fun showMoreWindow(view: View) {
        if (mMoreWindow != null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view)
    }

}
