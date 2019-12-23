package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.View.ActivityFragment
import com.example.yangzihang.makestar.View.VideoFragment
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.discover.view.DynamicFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_myself.*

class MyselfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_myself)
        val avatars = findViewById<ImageView>(R.id.se_portrait)
        val nickname = findViewById<TextView>(R.id.se_nickname)
        val message = findViewById<TextView>(R.id.se_message)
        val mystyle = findViewById<TextView>(R.id.se_myself)
        val fansNum = findViewById<TextView>(R.id.se_people_num)
        val popNum = findViewById<TextView>(R.id.se_pop_num)
        val focusNum = findViewById<TextView>(R.id.se_focus_num)
        val rank = findViewById<TextView>(R.id.se_rank)
        Glide.with(this)
            .load(CommonPreferences.avatars)
            .into(avatars)
        fansNum.text = CommonPreferences.fans_num
        mystyle.text = CommonPreferences.signature
        nickname.text = CommonPreferences.username
        popNum.text = CommonPreferences.year_hot_value
        focusNum.text = CommonPreferences.focus_num
        rank.text = CommonPreferences.rank
        if (CommonPreferences.sex=="男"){
            message.text = "♂" + " | " + CommonPreferences.age + " | " + CommonPreferences.city
        }else{
            message.text = "♀" + " | " + CommonPreferences.age + " | " + CommonPreferences.city
        }
        val dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        dynamicFragmentPagerAdapter.add("动态",ActivityFragment())
        dynamicFragmentPagerAdapter.add("视频",VideoFragment())
        se_view.adapter = dynamicFragmentPagerAdapter
        se_dynamic_pager_indicator2.setViewPager(se_view)
    }
}
