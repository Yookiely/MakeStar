package com.example.yangzihang.makestar.extensions

import com.example.auth.LoginActivity
import com.example.common.experimental.extensions.jumpchannel.Injector
import com.example.discover.DiscoverActivity
import com.example.userpage.MyUserActivity
import com.wingedvampires.attention.view.AttentionActivity
import com.wingedvampires.homepage.view.HomePageActivity

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
    }
}