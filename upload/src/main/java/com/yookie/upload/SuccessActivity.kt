package com.yookie.upload

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.yookie.common.experimental.extensions.jumpchannel.Transfer

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_success)
        var back = findViewById<TextView>(R.id.back_home)
        back.setOnClickListener {
            Transfer.startActivity(this,"HomePageActivity", Intent())
        }
    }
}
