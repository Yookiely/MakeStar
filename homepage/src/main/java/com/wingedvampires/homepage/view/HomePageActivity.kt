package com.wingedvampires.homepage.view

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
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
        val camra = findViewById<ImageView>(R.id.iv_homepage_camera)
        val localLayoutParams = window.attributes
        localLayoutParams.flags =
            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        attention.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "AttentionActivity", Intent())
        }
        discover.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "DiscoverActivity", Intent())
        }
        userpage.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "MyUserActivity", Intent())
        }

        homepageViewPager = findViewById(R.id.vp_homepage_main)
        dynamicPagerIndicator = findViewById(R.id.dynamic_pager_indicator2)

        iv_homepage_search.setOnClickListener {
            it.context.startActivity<GlobalSearchActivity>()
        }
        setViewPagerContent()

        alarm()
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

    private fun alarm() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val mRedPacket = HomePageService.getRedPacketInfo().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@HomePageActivity, "加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            if (!HomePageUtils.haveShowDialog) {
                showDialogOfRedpacket(mRedPacket.red_bag_count.toString())
            }
        }
    }

    private fun showDialogOfRedpacket(numOfDay: String) {
        val popupWindowView =
            LayoutInflater.from(this).inflate(R.layout.dialog_redpacket_alarm, null, false)
        val day = popupWindowView.findViewById<TextView>(R.id.tv_redpacket_day)
        val cancel = popupWindowView.findViewById<TextView>(R.id.tv_redpacket_cancel)
        val ok = popupWindowView.findViewById<TextView>(R.id.tv_redpacket_ok)
        day.text = "￥${numOfDay}"
        val popupWindow = PopupWindow(
            popupWindowView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        ok.setOnClickListener {
            popupWindow.dismiss()
            HomePageUtils.haveShowDialog = true
            Transfer.startActivityWithoutClose(this, "RedPacketActivity", Intent())
        }
        cancel.setOnClickListener {
            HomePageUtils.haveShowDialog = true
            popupWindow.dismiss()
        }

        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            isTouchable = true


            setBackgroundDrawable(BitmapDrawable())
            bgAlpha(0.5f)
            showAsDropDown(tb_main, Gravity.CENTER, 0, 0)
            setOnDismissListener {
                // popupWindow 隐藏时恢复屏幕正常透明度
                bgAlpha(1f)
            }
        }


    }

    private fun bgAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
    }
}
