package com.example.yangzihang.makestar

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_about_me.*

class AboutMeActivity : AppCompatActivity() {
    lateinit var protocol :TextView
    lateinit var security :TextView
    lateinit var company :TextView
    lateinit var account :Button
    lateinit var update : Button
    lateinit var judge :Button
    lateinit var back :ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_about_me)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        protocol = findViewById(R.id.about_protocol)
        security = findViewById(R.id.about_security)
        company =findViewById(R.id.about_name)
        account =findViewById(R.id.about_account)
        update = findViewById(R.id.about_update)
        judge = findViewById(R.id.about_judge)
        back = findViewById(R.id.about_us_back)
        back.setOnClickListener {
            onBackPressed()
        }
        protocol.setOnClickListener {
            val intent = Intent(this@AboutMeActivity, AgreementActivity::class.java)
            intent.putExtra("FLAG", "agreement")
            startActivity(intent)
        }
        security.setOnClickListener {
            val intent = Intent(this@AboutMeActivity, AgreementActivity::class.java)
            intent.putExtra("FLAG", "rule")
            startActivity(intent)
        }
        account.setOnClickListener {
            val intent = Intent(this@AboutMeActivity, MyselfActivity::class.java)
            intent.putExtra("userID", "50")
            startActivity(intent)
        }
    }
}
