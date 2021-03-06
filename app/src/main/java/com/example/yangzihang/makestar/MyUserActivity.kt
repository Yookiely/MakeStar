package com.example.yangzihang.makestar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.View.setUserText
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.auth.api.authSelfLiveData
import com.yookie.common.experimental.extensions.bindNonNull
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.common.experimental.extensions.morewindow.MoreWindow
import com.yookie.common.experimental.preference.CommonPreferences

class MyUserActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var image : ImageView
    private var mMoreWindow: MoreWindow? = null
    lateinit var nickname : TextView
    lateinit var portrait : ImageView
    lateinit var message : TextView
    lateinit var mystyle : TextView
    lateinit var fansNum : TextView
    lateinit var popNum : TextView
    lateinit var focusNum : TextView
    lateinit var rank : TextView
    lateinit var userId : TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_my_user)
        image = findViewById(R.id.iv_user_camera)
        image.setOnClickListener { v -> showMoreWindow(v) }
        portrait = findViewById<ImageView>(R.id.portrait)
        rank = findViewById<TextView>(R.id.rank)
        nickname = findViewById<TextView>(R.id.nickname)
        message = findViewById<TextView>(R.id.message)
        userId = findViewById<TextView>(R.id.user_id)
        mystyle = findViewById<TextView>(R.id.style)
        fansNum = findViewById<TextView>(R.id.people_num)
        popNum = findViewById<TextView>(R.id.pop_num)
        focusNum = findViewById<TextView>(R.id.focus_num)
        val enter = findViewById<TextView>(R.id.enter_oneself)
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val home = findViewById<TextView>(R.id.tv_bottom_homepage)
        val discover = findViewById<TextView>(R.id.tv_bottom_find)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        attention.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "AttentionActivity", Intent())
        }
        home.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "HomePageActivity", Intent())
        }
        discover.setOnClickListener {
            it.isEnabled = false
            Transfer.startActivity(this, "DiscoverActivity", Intent())
        }

        enter.setOnClickListener {
            val intent = Intent(this,MyselfActivity::class.java)
            intent.putExtra("userID",CommonPreferences.userid)
            startActivity(intent)
        }
        UserImp.getMessageNum(CommonPreferences.userid) {
            if (it.new_star_num + it.new_comment_num + it.new_message_num + it.new_user_message_num > 0) {
                CommonPreferences.newMessage = 1
            } else {
                CommonPreferences.newMessage = 0
            }
            CommonPreferences.apply {
                newStarMessage = it.new_star_num
                newUserMessage = it.new_user_message_num
                newSystemMessage = it.new_message_num
                newCommentMessage = it.new_comment_num
            }
        }
        recyclerView = findViewById(R.id.user_rec)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.withItems {
//            setUserText("历史排名",this@MyUserActivity,1,0)
            setUserText("我的消息",this@MyUserActivity,2,CommonPreferences.newMessage)
            setUserText("我的收藏",this@MyUserActivity,3,0)
            setUserText("我的红包", this@MyUserActivity, 7,0)
            //setUserText("粉丝圈",this@MyUserActivity,4,0)
            setUserText("设置",this@MyUserActivity,5,0)
//            setUserText("我的订单",this@MyUserActivity,5)
//            setUserText("商务合作",this@MyUserActivity,6)
        }
        authSelfLiveData.bindNonNull(this) {
            Glide.with(this)
                .load(it.data.avatar)
                .error(R.drawable.ms_no_pic)
                .into(portrait)
            it.data.apply {
                fansNum.text = this.fans_num.toString()
                mystyle.text = this.signature
                nickname.text = this.username
                userId.text = this.user_ID.toString()
                popNum.text = this.year_hot_value.toString()
                focusNum.text = this.follow_num.toString()
                rank.text = this.month_rank.toString()
                if (this.sex=="男"){
                    message.text = "♂" + " | " + this.age + " | " + this.city
                }else{
                    message.text = "♀" + " | " + this.age + " | " + this.city
                }
            }
        }
//        Glide.with(this)
//            .load(CommonPreferences.avatars)
//            .into(portrait)
//        fansNum.text = CommonPreferences.fans_num
//        mystyle.text = CommonPreferences.signature
//        nickname.text = CommonPreferences.username
//        userId.text = CommonPreferences.userid
//        popNum.text = CommonPreferences.year_hot_value
//        focusNum.text = CommonPreferences.focus_num
//        rank.text = CommonPreferences.rank
//        if (CommonPreferences.sex=="男"){
//            message.text = "♂" + " | " + CommonPreferences.age + " | " + CommonPreferences.city
//        }else{
//            message.text = "♀" + " | " + CommonPreferences.age + " | " + CommonPreferences.city
//        }


    }
    private fun showMoreWindow(view: View) {
        if (mMoreWindow == null) {
            mMoreWindow = MoreWindow(this)
            mMoreWindow!!.init()
        }

        mMoreWindow!!.showMoreWindow(view)
    }

    override fun onResume() {
        super.onResume()
        UserImp.getMessageNum(CommonPreferences.userid) {
            if (it.new_star_num + it.new_comment_num + it.new_message_num + it.new_user_message_num > 0) {
                CommonPreferences.newMessage = 1
            } else {
                CommonPreferences.newMessage = 0
            }
            CommonPreferences.apply {
                newStarMessage = it.new_star_num
                newUserMessage = it.new_user_message_num
                newSystemMessage = it.new_message_num
                newCommentMessage = it.new_comment_num
            }
            recyclerView.withItems {
                this.clear()
                setUserText("我的消息", this@MyUserActivity, 2, CommonPreferences.newMessage)
                setUserText("我的收藏", this@MyUserActivity, 3, 0)
                setUserText("我的红包", this@MyUserActivity, 7, 0)
                //setUserText("粉丝圈", this@MyUserActivity, 4, 0)
                setUserText("设置", this@MyUserActivity, 5, 0)
            }
        }

    }
}
