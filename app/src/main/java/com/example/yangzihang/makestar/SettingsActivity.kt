package com.example.yangzihang.makestar

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.View.setGreyText
import com.example.yangzihang.makestar.View.setNormalItem
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.preference.CommonPreferences

class SettingsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_settings)
        recyclerView = findViewById(R.id.settings_rec)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        recyclerView.withItems {
            setGreyText("账号")
            setNormalItem("账号设置"){
                startActivity(Intent(this@SettingsActivity,EditActivity::class.java))

            }
//            setNormalItem("邀请好友"){
//
//            }
//            setNormalItem("实名认证"){
//
//            }
            setGreyText("关于")
            setNormalItem("隐私协议"){
                val intent = Intent(this@SettingsActivity,AgreementActivity::class.java)
                intent.putExtra("FLAG","rule")
                startActivity(intent)
            }
            setNormalItem("用户协议"){
                val intent = Intent(this@SettingsActivity,AgreementActivity::class.java)
                intent.putExtra("FLAG","agreement")
                startActivity(intent)
            }
//            setNormalItem("意见反馈"){
//
//            }
            setNormalItem("关于V-star"){
                val intent = Intent(this@SettingsActivity,AgreementActivity::class.java)
                intent.putExtra("FLAG","about")
                startActivity(intent)
            }
            setGreyText("  ")
            setNormalItem("退出登录"){
                AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("真的要登出吗？")
                    .setPositiveButton("真的") { _, _ ->
                        PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity).edit().clear().apply()
                        CommonPreferences.clear()
                        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)

                        this@SettingsActivity.startActivity(intent)
                        this@SettingsActivity.finish()
                    }
                    .setNegativeButton("算了") { _, _ ->
                        Toast.makeText(this@SettingsActivity, "真爱啊 TAT...", Toast.LENGTH_SHORT).show()
                    }.create().show()
            }
        }

    }
}
