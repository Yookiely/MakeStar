package com.example.yangzihang.makestar.extensions

import com.alibaba.sdk.android.vodupload_demo.MultiUploadActivity
import com.aliyun.svideo.snap.SnapRecorderSetting
import com.example.yangzihang.makestar.ComplaintActivity
import com.example.yangzihang.makestar.MyUserActivity
import com.example.yangzihang.makestar.MyselfActivity
import com.example.yangzihang.makestar.RedPacketActivity
import com.wingedvampires.attention.view.AttentionActivity
import com.wingedvampires.attention.view.CommentsActivity
import com.wingedvampires.homepage.view.GlobalSearchActivity
import com.wingedvampires.homepage.view.HomePageActivity
import com.wingedvampires.videoplay.view.VideoPlayActivity
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.extensions.jumpchannel.Injector
import com.yookie.discover.DiscoverActivity
import com.yookie.upload.ReleaseActivity

object AppArchmage {

    /***需要进行跨二级模块的页面跳转的需要在这里进行注册
     *注册之后，通过
     * Transfer.startActivity(this, "MyUserActivity", Intent())
     * Transfer.startActivity(this, 目标activity名称的字符串, Intent())
     * 即可进行二级模块间的跳转
     */
    fun init() {
        Injector.inject("MyUserActivity", MyUserActivity::class.java)
        Injector.inject("HomePageActivity", HomePageActivity::class.java)
        Injector.inject("DiscoverActivity", DiscoverActivity::class.java)
        Injector.inject("AttentionActivity", AttentionActivity::class.java)
        Injector.inject("LoginActivity", LoginActivity::class.java)
        Injector.inject("VideoPlayActivity", VideoPlayActivity::class.java)
        Injector.inject("CommentsActivity", CommentsActivity::class.java)
        Injector.inject("RedPacketActivity", RedPacketActivity::class.java)
        Injector.inject("ComplaintActivity", ComplaintActivity::class.java)

        Injector.inject("SnapRecorderSetting",SnapRecorderSetting::class.java)
        Injector.inject("MultiUploadActivity", MultiUploadActivity::class.java)
        Injector.inject("ReleaseActivity", ReleaseActivity::class.java)
        Injector.inject("MyselfActivity", MyselfActivity::class.java)
        Injector.inject("GlobalSearchActivity", GlobalSearchActivity::class.java)
    }
}