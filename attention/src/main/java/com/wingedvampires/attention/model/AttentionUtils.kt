package com.wingedvampires.attention.model

import java.text.SimpleDateFormat
import java.util.*

object AttentionUtils {
    val INDEX_KEY = "key"

    fun formatTime(time: Float): String {
        val timeOfMS = time * 1000
        val date = Date(timeOfMS.toLong())
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date)
    }
}