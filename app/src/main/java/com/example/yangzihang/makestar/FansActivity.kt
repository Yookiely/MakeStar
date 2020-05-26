package com.example.yangzihang.makestar

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.network.UserImp
import com.example.yangzihang.makestar.View.FansCircleFragment
import com.example.yangzihang.makestar.View.FansPagerAdapter
import com.example.yangzihang.makestar.View.FansStarFragment
import com.yookie.upload.ReleaseActivity
import kotlinx.android.synthetic.main.activity_fans.*

class FansActivity : AppCompatActivity() {
    lateinit var mTabLayout: TabLayout
    lateinit var rank : TextView
    lateinit var userName : TextView
    lateinit var fansNum : TextView
    lateinit var hot :TextView
    lateinit var jionCircle :TextView
    lateinit var invitationNum : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fans)
        mTabLayout = findViewById(R.id.fans_table)
        val viewPager = findViewById<ViewPager>(R.id.fans_viewPager)
        val mPagerAdapter = FansPagerAdapter(supportFragmentManager)
        val hostUserID = intent.getStringExtra("userID")
        val avator =intent.getStringExtra("avatar")
        fans_back.setOnClickListener {
            onBackPressed()
        }
        fans_post.setOnClickListener {
            val intent = Intent(this,ReleaseActivity::class.java)
            intent.putExtra("isfans",true)
            startActivity(intent)
        }
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        rank=findViewById(R.id.fans_num)
        hot =findViewById(R.id.read_num)
        userName =findViewById(R.id.fans_user_nickname)
        fansNum =findViewById(R.id.fans_num)
        invitationNum =findViewById(R.id.invitation_num)
        jionCircle = findViewById(R.id.fans_jion_circle)
        jionCircle.setOnClickListener {
            UserImp.changeStatusOfFandom(hostUserID){
                if(it == -1){
                    jionCircle.text = "已加入"
                    Toast.makeText(this,"已加入",Toast.LENGTH_SHORT).show()
                }else{
                    jionCircle.text = "加入圈子"
                    Toast.makeText(this,"加入失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
        Glide.with(this)
            .load(avator)
            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
            .into(fans_user_portrait)
        UserImp.getFandomInfo(hostUserID) {
            hot.text =it.total_hot
            invitationNum.text =it.total_work_num.toString()
            fansNum.text =it.fans_num.toString()
            userName.text =it.host_username
            rank.text =it.month_rank.toString()
            if(it.is_into){
                jionCircle.text ="已加入"
            }else{
                jionCircle.text ="加入圈子"
            }
        }
        mPagerAdapter.apply {
            add(FansCircleFragment.newInstance(hostUserID.toInt()),"圈子动态")
            add(FansStarFragment(),"明星帖子")
        }
        viewPager.adapter = mPagerAdapter
        mTabLayout.setupWithViewPager(viewPager)
    }
}
