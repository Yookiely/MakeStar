package com.example.yangzihang.makestar

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.View.ActivityFragment
import com.example.yangzihang.makestar.View.VideoFragment
import com.example.yangzihang.makestar.network.UserImp
import com.wingedvampires.attention.model.AttentionService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.discover.view.DynamicFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_myself.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MyselfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_myself)
        var flag = false
        val avatars = findViewById<ImageView>(R.id.se_portrait)
        val nickname = findViewById<TextView>(R.id.se_nickname)
        val message = findViewById<TextView>(R.id.se_message)
        val mystyle = findViewById<TextView>(R.id.se_myself)
        val fansNum = findViewById<TextView>(R.id.se_people_num)
        val popNum = findViewById<TextView>(R.id.se_pop_num)
        val focusNum = findViewById<TextView>(R.id.se_focus_num)
        val rank = findViewById<TextView>(R.id.se_rank)
        val more = findViewById<ImageView>(R.id.page_more)
        val back = findViewById<ImageView>(R.id.page_back)
        val order = findViewById<Button>(R.id.page_order)
        val fans = findViewById<TextView>(R.id.page_to_fanscircle)
        var avatar = ""
        back.setOnClickListener { onBackPressed() }
        val userid = intent.getStringExtra("userID")
        Log.d("?????", userid)
        UserImp.getUserInfo(userid) {
            Log.d("ccc", userid)
            Glide.with(this)
                .load(it.avatar)
                .into(avatars)
            avatar = it.avatar
            nickname.text = it.username
            fansNum.text = it.fans_num.toString()
            mystyle.text = it.signature
            popNum.text = it.year_hot_value.toString()
            focusNum.text = it.follow_num.toString()
            rank.text = it.month_rank.toString()
            if (it.sex == "男") {
                message.text = "♂" + " | " + it.age + " | " + it.city
            } else {
                message.text = "♀" + " | " + it.age + " | " + it.city
            }
        }
        val localLayoutParams = window.attributes
        localLayoutParams.flags =
            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        more.setOnClickListener {
            showPopupWindow(userid, it)
        }
        fans.setOnClickListener {
            val intent =Intent(this@MyselfActivity,FansActivity::class.java)
            intent.putExtra("userID",userid)
            intent.putExtra("avatar",avatar)
            startActivity(intent)
        }
        if (userid == CommonPreferences.userid) {
            order.visibility = View.INVISIBLE
            fans.visibility = View.INVISIBLE
            Glide.with(this)
                .load(CommonPreferences.avatars)
                .error(R.drawable.ms_no_pic)
                .into(avatars)
            nickname.text =CommonPreferences.username
            fansNum.text = CommonPreferences.fans_num
            rank.text = CommonPreferences.rank
            mystyle.text = CommonPreferences.signature
            popNum.text =CommonPreferences.year_hot_value
            focusNum.text =CommonPreferences.focus_num
            if (CommonPreferences.sex=="男"){
                message.text = "♂" + " | " + CommonPreferences.age + " | " + CommonPreferences.city
            }else{
                message.text = "♀" + " | " + CommonPreferences.age + " | " + CommonPreferences.city
            }
        } else if (userid != CommonPreferences.userid) {
            launch(UI + QuietCoroutineExceptionHandler) {
                order.isEnabled = false
                val addCommonBody = AttentionService.addFollow(userid).awaitAndHandle {
                    it.printStackTrace()
                    Toast.makeText(this@MyselfActivity, "操作失败", Toast.LENGTH_SHORT).show()
                    order.isEnabled = true
                } ?: return@launch
                if (addCommonBody.error_code == 1) {
                    order.text = "取消关注"
                    flag = true
                }else if(addCommonBody.error_code == -1){
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody = AttentionService.deleteFollow(userid).awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@MyselfActivity, "操作失败", Toast.LENGTH_SHORT).show()
                            order.isEnabled = true
                        } ?: return@launch
                        if (addCommonBody.error_code == -1) {
                            order.text = "+关注"
                            flag = false
                        }
                    }
                }
                order.isEnabled = true
            }

            order.setOnClickListener {
                order.isEnabled = false
                if (flag) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody = AttentionService.deleteFollow(userid).awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@MyselfActivity, "操作失败", Toast.LENGTH_SHORT).show()
                            order.isEnabled = true
                        } ?: return@launch

                        Toast.makeText(this@MyselfActivity, addCommonBody.message, Toast.LENGTH_SHORT).show()

                        if (addCommonBody.error_code == -1) {
                            order.text = "+关注"
                            flag = false
                        }
                        order.isEnabled = true
                    }
                } else {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val addCommonBody = AttentionService.addFollow(userid).awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@MyselfActivity, "操作失败", Toast.LENGTH_SHORT).show()
                            order.isEnabled = true
                        } ?: return@launch

                        Toast.makeText(this@MyselfActivity, addCommonBody.message, Toast.LENGTH_SHORT).show()

                        if (addCommonBody.error_code == -1) {
                            order.text = "取消关注"
                            flag = true
                        }
                        order.isEnabled = true
                    }
                }
            }
        }
        val dynamicFragmentPagerAdapter = DynamicFragmentPagerAdapter(supportFragmentManager)
        dynamicFragmentPagerAdapter.add("视频", VideoFragment.newInstance(userid))
        dynamicFragmentPagerAdapter.add("动态", ActivityFragment.newInstance(userid.toInt()))
        se_view.adapter = dynamicFragmentPagerAdapter
        se_dynamic_pager_indicator2.setViewPager(se_view)


    }

    fun showPopupWindow(userid: String, more: View) {
        val contentView = LayoutInflater.from(this).inflate(R.layout.pop_page_more, null)
        val mPopupWindow = PopupWindow(
            contentView,
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT,
            true
        )
        mPopupWindow.contentView = contentView
        val leaveMes = contentView.findViewById<TextView>(R.id.page_leave)
        val leaveSha = contentView.findViewById<TextView>(R.id.page_share)
        leaveSha.visibility = View.GONE
        if (userid == CommonPreferences.userid) {
            leaveMes.text = "编辑个人资料"
            leaveSha.text = "我的名片"
            leaveMes.setOnClickListener {
                val intent = Intent(this,EditActivity::class.java)
                startActivity(intent)
                mPopupWindow.dismiss()
            }
        }else {
            leaveMes.setOnClickListener {
                val intent = Intent(this, LeaveMessageActivity::class.java)
                intent.putExtra("userID", userid)
                startActivity(intent)
                mPopupWindow.dismiss()
            }
        }
        mPopupWindow.showAsDropDown(more, -10, -10, Gravity.START)
    }
}
