package com.wingedvampires.homepage.model

import com.yookie.common.experimental.preference.hawk

object HomePageUtils {
    const val INDEX_KEY = "page"
    const val WORK_TYPE_ID_OF_RECOMMEND = 0
    const val VIDEO_PALY_WORKID = "videopalyWorkId"
    const val SEARCH_TYPE = "searchType"
    const val SEARCH_CONTENT = "searchContent"
    const val SEARCH_USER = "searchUser"
    const val SEARCH_VIDEO = "searchVideo"
    var haveShowDialog by hawk("have_show_dialog", false)

    val typeList = hashMapOf<Int, String>()



    fun format(num: String?): String? {

        val length = num?.length ?: return null

        return when {
            length < 5 -> num
            length < 8 -> "${num.substring(0, length - 4)}.${num.substring(length - 4, length - 2)}w"
            else -> "${num.substring(0, length - 7)}.${num.substring(length - 7, length - 5)}kw"
        }
    }
}