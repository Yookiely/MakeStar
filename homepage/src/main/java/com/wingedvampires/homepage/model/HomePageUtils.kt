package com.wingedvampires.homepage.model

import com.example.common.experimental.preference.hawk

object HomePageUtils {
    const val INDEX_KEY = "page"
    const val WORK_TYPE_ID_OF_RECOMMEND = 0
    var userHabit by hawk("USER_HABIT", mutableListOf<Int>()) // 用户的喜好类型ID
    var userHistory by hawk("USER_HISTORY", mutableListOf<String>()) // 用户爱看的播主ID
    val typeList = hashMapOf<Int, String>()

    fun setAndGetUserHabit(habitID: Int? = null): String {
        if (habitID != null) {
            val temp = mutableListOf<Int>().also { it.addAll(userHabit) }
            temp[habitID - 1]++
            userHabit = mutableListOf<Int>().also { it.addAll(temp) }
        }

        return userHabit.toString().replace(" ", "").substring(1, 2 * userHabit.size)
    }

    fun setAndGetUserHistory(historyId: String? = null): String {
        if (historyId != null && !userHistory.contains(historyId)) {
            val temp = mutableListOf<String>().also { it.addAll(userHistory) }
            temp.add(historyId)
            userHistory = mutableListOf<String>().also { it.addAll(temp) }
        }

        return userHistory.toString().replace(" ", "").substring(1, 2 * userHistory.size)
    }

    fun format(num: String?): String? {

        val length = num?.length ?: return null

        return if (length < 5) {
            num
        } else if (length < 8) {
            "${num.substring(0, length - 4)}.${num.substring(length - 4, length - 2)}w"
        } else {
            "${num.substring(0, length - 7)}.${num.substring(length - 7, length - 5)}kw"
        }
    }
}