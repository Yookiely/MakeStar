package com.wingedvampires.attention.model

object VideoPlayUtils {

    const val VIDEO_PALY_WORKID = "videopalyWorkId"
    const val VIDEO_PALY_COMMENT = "commentIndex"

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
}