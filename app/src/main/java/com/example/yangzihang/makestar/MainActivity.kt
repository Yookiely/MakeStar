package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.example.auth.LoginActivity
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        startActivity<LoginActivity>()
    }
}
