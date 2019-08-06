package com.example.common

object Injector {
    private val sClassMap: HashMap<String, Class<*>> = HashMap()

    fun inject(name: String, clazz: Class<*>) {
        sClassMap.put(name, clazz)
    }

    fun getClass(className: String): Class<*>? {
        return sClassMap.get(className)
    }
}