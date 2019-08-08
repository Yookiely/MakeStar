package com.example.auth.api

import com.example.common.AuthData
import com.example.common.AuthService
import com.example.common.experimental.cache.*
import com.example.common.experimental.extensions.awaitAndHandle
import com.example.common.experimental.network.CommonBody
import com.example.common.experimental.preference.CommonPreferences
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


val authSelfLocalCache = Cache.hawk<CommonBody<AuthData>>("AUTH_SELF")
val authSelfRemoteCache = Cache.from(AuthService.Companion::authSelf)
val authSelfLiveData = RefreshableLiveData.use(authSelfLocalCache, authSelfRemoteCache) {
    it.data?.apply {
        CommonPreferences.username = this.username
        CommonPreferences.userid = this.user_ID
        CommonPreferences.avatars = this.avatar
        CommonPreferences.sex = this.sex
        CommonPreferences.age = this.age
        CommonPreferences.fans_num = this.fans_num
        CommonPreferences.signature = this.signature
        CommonPreferences.week_hot_value = this.week_hot_value
        CommonPreferences.month_hot_value = this.month_hot_value
        CommonPreferences.year_hot_value = this.year_hot_value
        CommonPreferences.spotted = this.spotted
        CommonPreferences.city = this.city




    }
//    CommonPreferences.username = it.data.username
//    CommonPreferences.studentid = it.studentid
//    CommonPreferences.isBindLibrary = it.accounts.lib
//    CommonPreferences.isBindTju = it.accounts.tju
//    CommonPreferences.dropOut = it.dropout
//    CommonPreferences.realName = it.realname
//    CrashReport.setUserId(it.twtuname)
}
fun login(username: String, password: String, callback: suspend (RefreshState<Unit>) -> Unit = {}) {
    launch(UI) {
        AuthService.getToken(username, password).awaitAndHandle {
            callback(RefreshState.Failure(it))
        }?.data?.let {
            CommonPreferences.token = it.token
            CommonPreferences.password = password
            CommonPreferences.isLogin = true
//            callback(RefreshState.Success(Unit))
        }
    }
}