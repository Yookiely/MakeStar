package com.example.auth.api

import com.example.common.AuthService
import com.example.common.experimental.cache.RefreshState
import com.example.common.experimental.extensions.awaitAndHandle
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

fun login(username: String, password: String, callback: suspend (RefreshState<Unit>) -> Unit = {}) {
    launch(UI) {
        AuthService.getToken(username, password).awaitAndHandle {
            callback(RefreshState.Failure(it))
        }?.data?.let {
//            CommonPreferences.token = it.token
//            CommonPreferences.password = password
//            CommonPreferences.isLogin = true
//            callback(RefreshState.Success(Unit))
        }
    }
}