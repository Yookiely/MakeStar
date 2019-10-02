package com.yookie.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.wingedvampires.homepage.view.HomePageActivity
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.yangzihang.makestar.extensions.AppArchmage
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        AppArchmage.init()
        if (CommonPreferences.isLogin) {
            startActivity<HomePageActivity>()
            finish()
        } else startActivity<LoginActivity>()
    }
}
