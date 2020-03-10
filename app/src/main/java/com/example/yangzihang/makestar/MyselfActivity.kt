package com.example.yangzihang.makestar

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
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
        val more =findViewById<ImageView>(R.id.page_more)
        val back =findViewById<ImageView>(R.id.page_back)
        val order = findViewById<Button>(R.id.page_order)
        val fans =findViewById<TextView>(R.id.page_to_fanscircle)
        back.setOnClickListener { onBackPressed() }
        val userid = intent.getStringExtra("userID")
        more.setOnClickListener {
            showPopupWindow(userid,it)
        }
        if(userid == CommonPreferences.userid){
            order.visibility = View .INVISIBLE
            fans.visibility =View.INVISIBLE
        }
        Log.d("?????",userid)
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
    fun showPopupWindow(userid : String,more : View){
        val contentView =LayoutInflater.from(this).inflate(R.layout.pop_page_more,null)
        val mPopupWindow = PopupWindow(contentView,ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT,true)
        mPopupWindow.contentView = contentView
        val leaveMes = contentView.findViewById<TextView>(R.id.page_leave)
        val leaveSha =contentView.findViewById<TextView>(R.id.page_share)
        if(userid == CommonPreferences.userid){
            leaveMes.text ="编辑个人资料"
            leaveSha.text = "我的名片"
        }
        leaveMes.setOnClickListener {
            val intent = Intent(this,LeaveMessageActivity::class.java)
            intent.putExtra("userID",userid)
            startActivity(intent)
            mPopupWindow.dismiss()
        }
        mPopupWindow.showAsDropDown(more,-10,-10,Gravity.START)
    }
}
