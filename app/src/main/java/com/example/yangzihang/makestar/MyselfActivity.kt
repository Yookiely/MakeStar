package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.View.ActivityFragment
import com.example.yangzihang.makestar.View.VideoFragment
import com.example.yangzihang.makestar.network.UserImp
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
        val userid = intent.getStringExtra("userID")
        UserImp.getUserInfo(userid){
            Glide.with(this)
                .load(it.avatar)
                .into(avatars)
            nickname.text = it.username
            fansNum.text = it.fans_num.toString()
            mystyle.text = it.signature
            popNum.text = it.year_hot_value.toString()
            focusNum.text = it.follow_num.toString()
            rank.text = it.month_rank.toString()
            if (it.sex=="男"){
                message.text = "♂" + " | " + it.age + " | " + it.city
            }else{
                message.text = "♀" + " | " + it.age + " | " + it.city
            }

        }
        val dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        dynamicFragmentPagerAdapter.add("动态",ActivityFragment.newInstance(userid.toInt()))
        dynamicFragmentPagerAdapter.add("视频",VideoFragment.newInstance(userid))
        se_view.adapter = dynamicFragmentPagerAdapter
        se_dynamic_pager_indicator2.setViewPager(se_view)
    }
}
