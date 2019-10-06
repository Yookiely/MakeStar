package com.wingedvampires.homepage.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageService
import com.wingedvampires.homepage.model.HomePageUtils
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.common.experimental.extensions.morewindow.MoreWindow
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.startActivity

class HomePageActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private var mMoreWindow: MoreWindow? = null
    private lateinit var homepageViewPager: ViewPager
    private lateinit var dynamicPagerIndicator: DynamicPagerIndicator
    private lateinit var dynamicFragmentPagerAdapter: DynamicFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_homepage)
        val toolbar = findViewById<Toolbar>(R.id.tb_main)
        setSupportActionBar(toolbar)
        image = findViewById(R.id.iv_homepage_camera)
        image.setOnClickListener { v -> showMoreWindow(v) }
//        val homepage = findViewById<TextView>(R.id.tv_bottom_homepage)
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_find)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)
        attention.setOnClickListener { Transfer.startActivity(this, "AttentionActivity", Intent()) }
        discover.setOnClickListener { Transfer.startActivity(this, "DiscoverActivity", Intent()) }
        userpage.setOnClickListener { Transfer.startActivity(this, "MyUserActivity", Intent()) }
        homepageViewPager = findViewById(R.id.vp_homepage_main)
        dynamicPagerIndicator = findViewById(R.id.dynamic_pager_indicator2)

        iv_homepage_search.setOnClickListener {
            it.context.startActivity<GlobalSearchActivity>()
        }
        setViewPagerContent()
    }

    private fun setViewPagerContent() {
        dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        launch(UI + QuietCoroutineExceptionHandler) {
            //音乐、舞蹈、搞笑、颜值、小剧场和其他
            val workTypes = HomePageService.getWorkTypes().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@HomePageActivity, "加载失败", Toast.LENGTH_SHORT).show()
                Log.d("shibaile", "shibiale")
            }?.data ?: return@launch

            //初始化用户爱好
            CommonPreferences.initHabit(workTypes.size)

            workTypes.forEach { workType ->
                HomePageUtils.typeList[workType.work_type_ID] = workType.work_type_name
            }

            //先添加推荐页面
            dynamicFragmentPagerAdapter.add(
                "推荐",
                HomePageFragment.newInstance(HomePageUtils.WORK_TYPE_ID_OF_RECOMMEND)
            )

            workTypes.forEach { workType ->
                dynamicFragmentPagerAdapter.add(
                    workType.work_type_name,
                    HomePageFragment.newInstance(workType.work_type_ID)
                )
            }
            homepageViewPager.adapter = dynamicFragmentPagerAdapter
            dynamicPagerIndicator.setViewPager(homepageViewPager)

        }


    }

    private fun showMoreWindow(view: View) {
        if (mMoreWindow == null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view)
    }

}
