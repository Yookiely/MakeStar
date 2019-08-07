package com.example.common.experimental

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import org.jetbrains.anko.defaultSharedPreferences
import java.lang.ref.WeakReference

object CommonContext {

    private var applicationReference: WeakReference<Application>? = null

    fun registerApplication(application: Application) {
        applicationReference = WeakReference(application)
        Hawk.init(application).build()
    }

    val application: Application
        get() = applicationReference?.get()
            ?: throw IllegalStateException("Application should be registered in CommonContext.")

    val applicationVersion: String by lazy {
        application.packageManager.getPackageInfo(application.packageName, 0).versionName
    }

    val defaultSharedPreferences: SharedPreferences by lazy { application.defaultSharedPreferences }

    private var activityClasses: MutableMap<String, Class<out Activity>> = mutableMapOf()

    fun registerActivity(name: String, clazz: Class<out Activity>) {
        activityClasses[name] = clazz
    }

    fun startActivity(context: Context = this.application, name: String, block: Intent.() -> Unit = {}) =
        activityClasses[name]?.let { context.startActivity(Intent(context, it).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).apply(block)) }
            ?: throw IllegalStateException("Activity $name should be registered in CommonContext.")

    fun getActivity(name: String): Class<out Activity>? = activityClasses[name]

}

fun Context.startActivity(name: String, block: Intent.() -> Unit = {}) = CommonContext.startActivity(this, name, block)