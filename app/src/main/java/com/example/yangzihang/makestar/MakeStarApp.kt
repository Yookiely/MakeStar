package com.example.yangzihang.makestar

import android.support.multidex.MultiDexApplication
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.CommonContext

class MakeStarApp : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()

        CommonContext.apply {
            registerApplication(this@MakeStarApp)
            registerActivity("login", LoginActivity::class.java)
        }




    }
}