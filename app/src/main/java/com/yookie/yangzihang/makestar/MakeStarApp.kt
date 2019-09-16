package com.yookie.yangzihang.makestar

import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport
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