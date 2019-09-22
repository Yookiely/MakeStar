package com.wingedvampires.attention.model

import com.yookie.common.experimental.preference.hawk
import java.text.SimpleDateFormat
import java.util.*

object AttentionUtils {
    const val COMMENT_INDEX = "commentIndex"
    const val SECOND_COMMENT_INDEX = "secondCommentIndex"
    var searchHistory by hawk("ATTENTION_SEARCH_HISTORY", mutableListOf<DataOfUser>())

    fun formatTime(time: Float): String {
        val timeOfMS = time //* 1000
        val date = Date(timeOfMS.toLong())
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+0")
        return dateFormat.format(date)
    }

    fun format(num: String?): String? {
        val length = num?.length ?: return null

        return when {
            length < 5 -> num
            length < 8 -> "${num.substring(0, length - 4)}.${num.substring(
                length - 4,
                length - 2
            )}w"
            else -> "${num.substring(0, length - 7)}.${num.substring(length - 7, length - 5)}kw"
        }
    }

    fun setSearchHistory(dataOfUser: DataOfUser) {
        val temp = mutableListOf<DataOfUser>().also { it.addAll(searchHistory) }
        if (temp.contains(dataOfUser)) {
            temp.remove(dataOfUser)
        }
        temp.add(0, dataOfUser)
        searchHistory = mutableListOf<DataOfUser>().also { it.addAll(temp) }
    }
}