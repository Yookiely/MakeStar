package com.wingedvampires.homepage.model

import com.example.common.experimental.preference.hawk

object HomePageUtils {
    const val INDEX_KEY = "page"
    const val WORK_TYPE_ID_OF_RECOMMEND = 0
    var userHabit by hawk("USER_HABIT", mutableListOf<Int>()) // 用户的喜好类型ID
    var userHistory by hawk("USER_HISTORY", mutableListOf<Int>()) // 用户爱看的播主ID

    fun setAndGetUserHabit(habitID: Int? = null): String {
        if (habitID != null) {
            val temp = mutableListOf<Int>().also { it.addAll(userHabit) }
            temp[habitID - 1]++
            userHabit = mutableListOf<Int>().also { it.addAll(temp) }
        }

        return userHabit.toString().replace(" ", "").substring(1, 2 * userHabit.size)
    }

    fun setAndGetUserHistory(historyId: Int? = null): String {
        if (historyId != null && !userHistory.contains(historyId)) {
            val temp = mutableListOf<Int>().also { it.addAll(userHistory) }
            temp.add(historyId)
            userHistory = mutableListOf<Int>().also { it.addAll(temp) }
        }

        return userHistory.toString().replace(" ", "").substring(1, 2 * userHistory.size)
    }
}