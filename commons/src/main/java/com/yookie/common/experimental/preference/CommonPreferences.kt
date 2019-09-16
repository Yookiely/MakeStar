package com.yookie.common.experimental.preference

import com.yookie.common.experimental.CommonContext
import com.orhanobut.hawk.Hawk

object CommonPreferences {

    var token by hawk("token", "")

    var isLogin by hawk("is_login", false)

    var userid by hawk("user_num", "")

    var username by hawk("user_id", "")

    var password by hawk("user_pwd", "")

    var sex by hawk("user_gender", "")

    var age by hawk("user_age", 0)

    var avatars by hawk("img_url", "")

    var focus_num by hawk("focus_num",0)

    var fans_num by hawk("fans_num", "")

    var week_hot_value by hawk("week_hot_value", "")

    var month_hot_value by hawk("month_hot_value", "")

    var year_hot_value by hawk("year_hot_value", "")

    var signature by hawk("signature", "")

    var spotted by hawk("spotted", "")

    var city by hawk("city", "")
    var rank by hawk("rank",1)

    var userHabit by hawk("USER_HABIT", mutableListOf<Int>()) // 用户的喜好类型ID

    var userHistory by hawk("USER_HISTORY", mutableListOf<String>()) // 用户看过的ID

    //获得和存储用户喜好
    fun setAndGetUserHabit(habitID: Int? = null): String {
        if (habitID != null) {
            val temp = mutableListOf<Int>().also { it.addAll(userHabit) }
            temp[habitID - 1]++
            userHabit = mutableListOf<Int>().also { it.addAll(temp) }
        }

        return userHabit.toString().replace(" ", "").substring(1, 2 * userHabit.size)
    }

    //获得和存储看过的ID
    fun setAndGetUserHistory(historyId: String? = null): String {
        if (historyId != null && !userHistory.contains(historyId)) {
            var temp = mutableListOf<String>().also { it.addAll(userHistory) }
            temp.add(historyId)
            if (temp.size > 150) {
                temp = temp.subList(75, temp.size - 1) //保留一半历史数据
            }
            userHistory = mutableListOf<String>().also { it.addAll(temp) }
        }

        return userHistory.toString().replace(" ", "").substring(1, 2 * userHistory.size)
    }

    fun clear() {
        Hawk.deleteAll()
        CommonContext.defaultSharedPreferences.edit().clear().apply()
    }

}