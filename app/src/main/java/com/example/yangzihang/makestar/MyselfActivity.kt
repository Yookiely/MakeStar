package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window

class MyselfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_myself)
    }
}
