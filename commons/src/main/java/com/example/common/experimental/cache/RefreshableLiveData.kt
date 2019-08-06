package com.example.common.experimental.cache

import android.arch.lifecycle.LiveData


enum class CacheIndicator {
    LOCAL, REMOTE
}

abstract class RefreshableLiveData<V, I> : LiveData<V>() {
//    protected var state : RefreshState<I>?
//        get() = stateLiveData.value?.message
//        set(value) {
//            stateLiveData.value = if (value == null) null else ConsumableMessage(value)
//        }
//    private val mutableState = MutableLiveData<ConsumableMessage<RefreshState<I>>>()
//    val stateLiveData: LiveData<ConsumableMessage<RefreshState<I>>> = mutableState

    abstract fun refresh(vararg indicators: I, callback: suspend (RefreshState<CacheIndicator>) -> Unit = {})
    abstract fun cancel()

    companion object
}

sealed class RefreshState<M> {
    class Success<M>(val message: M) : RefreshState<M>()
    class Failure<M>(val throwable: Throwable) : RefreshState<M>()
    class Refreshing<M> : RefreshState<M>()
}