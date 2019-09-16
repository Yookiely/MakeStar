package com.yookie.common.experimental.cache

import android.arch.lifecycle.LiveData
import android.content.Context
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.asReference
import org.json.JSONObject
import retrofit2.HttpException

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

/**
 * 封装标准的错误情况
 */
typealias HttpExceptionHandler = (errCode: Int, httpErrCode: Int, message: String, throwable: Throwable) -> Unit

fun HttpException.handleError(block: HttpExceptionHandler) {
    try {
        val exception = this
        val jsonObject = JSONObject(exception.response().errorBody()?.string())
        val err_code = jsonObject.getInt("error_code") ?: -1
        val message = jsonObject.getString("message") ?: ""
        block.invoke(err_code, exception.code(), message, exception)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <M> RefreshState.Failure<M>.handleError(block: HttpExceptionHandler) {
    if (throwable !is HttpException) {
        block.invoke(0, 0, "拉取数据出现错误", throwable)
        return
    }
    val exception = throwable
    exception.handleError(block)
}

//fun Context.simpleCallback(success: String? = "加载成功", error: String? = "发生错误", refreshing: String? = "加载中"): suspend (RefreshState<*>) -> Unit =
//    with(this.asReference()) {
//        {
//            when (it) {
//                is RefreshState.Success -> if (success != null) Toasty.success(this(), success).show()
//                is RefreshState.Failure -> if (error != null) Toasty.error(this(), "$error ${it.throwable.message}！${it.javaClass.name}").show()
//                is RefreshState.Refreshing -> if (refreshing != null) Toasty.normal(this(), "$refreshing...").show()
//            }
//        }
//    }

fun <V : Any> RefreshableLiveData.Companion.use(local: Cache<V>, remote: Cache<V>) =
    object : RefreshableLiveData<V, CacheIndicator>() {

        private var running: Job? = null

        override fun refresh(vararg indicators: CacheIndicator, callback: suspend (RefreshState<CacheIndicator>) -> Unit) {
            if (running?.isActive == true) return
            running = launch(UI + QuietCoroutineExceptionHandler) {

                callback(RefreshState.Refreshing())

                val handler: suspend (Throwable) -> Unit = { callback(RefreshState.Failure(it)) }

                val localDeferred =
                    if (CacheIndicator.LOCAL in indicators)
                        local.get() else null

                val remoteDeferred =
                    if (CacheIndicator.REMOTE in indicators)
                        remote.get() else null

                val localValue = localDeferred?.awaitAndHandle(handler)?.also {
                    value = it
                    callback(RefreshState.Success(CacheIndicator.LOCAL))
                }

                remoteDeferred?.awaitAndHandle(handler)?.takeIf { localValue != it }?.also {
                    value = it
                    callback(RefreshState.Success(CacheIndicator.REMOTE))

                    local.set(it).awaitAndHandle(handler)
                }

            }.apply { invokeOnCompletion { running = null } }
        }

        override fun cancel() {
            running?.cancel()
        }

        override fun onActive() {
            refresh(CacheIndicator.LOCAL, CacheIndicator.REMOTE)
        }

        override fun onInactive() {
            cancel()
        }

    }

fun <V : Any> RefreshableLiveData.Companion.use(local: Cache<V>, remote: Cache<V>, callbackOnEach: suspend (V) -> Unit) =
    object : RefreshableLiveData<V, CacheIndicator>() {

        private var running: Job? = null

        override fun refresh(vararg indicators: CacheIndicator, callback: suspend (RefreshState<CacheIndicator>) -> Unit) {
            if (running?.isActive == true) return
            running = launch(UI) {

                callback(RefreshState.Refreshing())

                val handler: suspend (Throwable) -> Unit = { callback(RefreshState.Failure(it)) }

                val localDeferred =
                    if (CacheIndicator.LOCAL in indicators)
                        local.get() else null

                val remoteDeferred =
                    if (CacheIndicator.REMOTE in indicators)
                        remote.get() else null

                val localValue = localDeferred?.awaitAndHandle(handler)?.also {
                    value = it
                    callbackOnEach(it)
                    callback(RefreshState.Success(CacheIndicator.LOCAL))
                }

                remoteDeferred?.awaitAndHandle(handler)?.takeIf { localValue != it }?.also {
                    value = it
                    callbackOnEach(it)
                    callback(RefreshState.Success(CacheIndicator.REMOTE))

                    local.set(it).awaitAndHandle(handler)
                }

            }.apply { invokeOnCompletion { running = null } }
        }

        override fun cancel() {
            running?.cancel()
        }

        override fun onActive() {
            refresh(CacheIndicator.LOCAL, CacheIndicator.REMOTE)
        }

        override fun onInactive() {
            cancel()
        }

    }

fun <V : Any> RefreshableLiveData.Companion.retrofit(remote: Cache<V>) = object : RefreshableLiveData<V, CacheIndicator>() {
    private var running: Job? = null

    override fun refresh(vararg indicators: CacheIndicator, callback: suspend (RefreshState<CacheIndicator>) -> Unit) {
        if (running?.isActive == true) return
        running = launch(UI + QuietCoroutineExceptionHandler) {

            callback(RefreshState.Refreshing())

            val handler: suspend (Throwable) -> Unit = { callback(RefreshState.Failure(it)) }


            val remoteDeferred =
                if (CacheIndicator.REMOTE in indicators)
                    remote.get() else null

            remoteDeferred?.awaitAndHandle(handler)?.also {
                value = it
                callback(RefreshState.Success(CacheIndicator.REMOTE))
            }

        }.apply { invokeOnCompletion { running = null } }
    }

    override fun cancel() {
        running?.cancel()
    }

    override fun onActive() {
        refresh(CacheIndicator.REMOTE)
    }

    override fun onInactive() {
        cancel()
    }

}