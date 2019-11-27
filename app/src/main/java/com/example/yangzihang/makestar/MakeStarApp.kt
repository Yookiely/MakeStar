package com.example.yangzihang.makestar

import android.support.multidex.MultiDexApplication
import android.util.Log
import com.aliyun.common.httpfinal.QupaiHttpFinal
import com.aliyun.sys.AlivcSdkCore
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.CommonContext

class MakeStarApp : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        QupaiHttpFinal.getInstance().initOkHttpFinal()
        AlivcSdkCore.register(applicationContext)
        AlivcSdkCore.setLogLevel(AlivcSdkCore.AlivcLogLevel.AlivcLogDebug)
        CommonContext.apply {
            registerApplication(this@MakeStarApp)
            registerActivity("login", LoginActivity::class.java)
        }




    }
}