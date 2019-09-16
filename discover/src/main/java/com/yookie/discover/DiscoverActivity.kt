package com.yookie.discover

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.yookie.common.experimental.extensions.jumpchannel.Transfer

class DiscoverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)
        val attention = findViewById<TextView>(R.id.tv_bottom_attention)
        val discover = findViewById<TextView>(R.id.tv_bottom_homepage)
        val userpage = findViewById<TextView>(R.id.tv_bottom_individual)
        attention.setOnClickListener { Transfer.startActivity(this, "AttentionActivity", Intent()) }
        discover.setOnClickListener { Transfer.startActivity(this, "HomePageActivity", Intent()) }
        userpage.setOnClickListener { Transfer.startActivity(this, "MyUserActivity", Intent()) }


    }
}
