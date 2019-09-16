package com.yookie.common.experimental.extensions.jumpchannel

object Injector {
    private val sClassMap: HashMap<String, Class<*>> = HashMap()

    fun inject(name: String, clazz: Class<*>) {
        sClassMap[name] = clazz
    }

    fun getClass(className: String): Class<*>? = sClassMap[className]
}