package com.yookie.common.experimental.extensions.jumpchannel

import android.app.Activity
import android.content.Intent
import android.text.TextUtils


object Transfer {

    fun startActivity(activity: Activity, path: String, intent: Intent) {
        val clazz = parsePath(path)
        if (clazz == null || !Activity::class.java.isAssignableFrom(clazz)) {
            throw IllegalStateException("con't find the class!")
        }
        intent.setClass(activity, clazz)
        activity.startActivity(intent)
        activity.finish()
    }

    private fun parsePath(path: String): Class<*>? {
        if (TextUtils.isEmpty(path)) {
            throw IllegalArgumentException("path must not null!")
        }

        return Injector.getClass(path)
    }
}