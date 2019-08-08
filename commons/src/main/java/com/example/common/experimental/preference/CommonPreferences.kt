package com.example.common.experimental.preference

import com.example.common.experimental.CommonContext
import com.orhanobut.hawk.Hawk

object CommonPreferences {

    var token by hawk("token", "")

    var isLogin by hawk("is_login", false)

    var userid by hawk("user_num", "")

    var username by hawk("user_id", "")

    var password by hawk("user_pwd", "")

    var sex by hawk("user_gender","")

    var age by hawk("user_age", 0)

    var avatars by hawk("img_url", "")

    var fans_num by hawk("fans_num", "")

    var week_hot_value by hawk("week_hot_value","")

    var month_hot_value by hawk("month_hot_value","")

    var year_hot_value by hawk("year_hot_value","")

    var signature by hawk("signature","")

    var spotted by hawk("spotted","")

    var city by hawk("city","")



    fun clear() {
        Hawk.deleteAll()
        CommonContext.defaultSharedPreferences.edit().clear().apply()
    }

}