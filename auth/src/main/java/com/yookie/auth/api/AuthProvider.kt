package com.yookie.auth.api

import com.yookie.common.AuthData
import com.yookie.common.AuthIt
import com.yookie.common.AuthService
import com.yookie.common.experimental.cache.*
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.network.CommonBody
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


val authSelfLocalCache = Cache.hawk<AuthIt>("AUTH_SELF")
val authSelfRemoteCache = Cache.from(AuthService.Companion::authSelf)
val authSelfLiveData = RefreshableLiveData.use(authSelfLocalCache, authSelfRemoteCache) {
    it.data.apply {
        CommonPreferences.username = this.username
        CommonPreferences.userid = this.user_ID.toString()
        CommonPreferences.sex = this.sex
        CommonPreferences.age = this.age.toString()
        CommonPreferences.fans_num = this.fans_num.toString()
        CommonPreferences.signature = this.signature
        CommonPreferences.avatars = this.avatar
        CommonPreferences.week_hot_value = this.week_hot_value.toString()
        CommonPreferences.month_hot_value = this.month_hot_value.toString()
        CommonPreferences.year_hot_value = this.year_hot_value.toString()
        CommonPreferences.city = this.city
        CommonPreferences.rank = this.month_rank.toString()
        CommonPreferences.focus_num = this.follow_num.toString()
        CommonPreferences.tags = if (this.tags == null) "" else this.tags!!

        if (this.birthday != null) {
            CommonPreferences.birthday =
                "${this.birthday!!.year}-${this.birthday!!.month}-${this.birthday!!.day}"
        } else {
            CommonPreferences.birthday = ""
        }

    }
}

fun login(username: String, password: String, callback: suspend (RefreshState<Unit>) -> Unit = {}) {
    launch(UI) {
        AuthService.getToken(username, password).awaitAndHandle {
            callback(RefreshState.Failure(it))
        }?.data?.let {
            CommonPreferences.token = it.token
            CommonPreferences.password = password
            CommonPreferences.isLogin = true
            CommonPreferences.username = it.username
            CommonPreferences.userid = it.user_ID.toString()
            CommonPreferences.sex = it.sex
            CommonPreferences.age = it.age.toString()
            CommonPreferences.fans_num = it.fans_num.toString()
            CommonPreferences.signature = it.signature
            CommonPreferences.avatars = it.avatar
            CommonPreferences.week_hot_value = it.week_hot_value.toString()
            CommonPreferences.month_hot_value = it.month_hot_value.toString()
            CommonPreferences.year_hot_value = it.year_hot_value.toString()
            CommonPreferences.city = it.city
            CommonPreferences.rank = it.month_rank.toString()
            CommonPreferences.focus_num = it.follow_num.toString()
            CommonPreferences.tags = if (it.tags == null) "" else it.tags!!

            if (it.birthday != null) {
                CommonPreferences.birthday =
                    "${it.birthday!!.year}-${it.birthday!!.month}-${it.birthday!!.day}"
            } else {
                CommonPreferences.birthday = ""
            }


            callback(RefreshState.Success(Unit))
        }
    }
}

fun loginForWechat(wechatOpenID: String, callback: suspend (RefreshState<String>) -> Unit = {}) {
    launch(UI) {
        AuthService.loginForWechat(wechatOpenID).awaitAndHandle {
            callback(RefreshState.Failure(it))
        }?.data?.let {
            CommonPreferences.token = it.token
            CommonPreferences.isWechat = true
            CommonPreferences.isLogin = true
            CommonPreferences.wechat_openid = wechatOpenID
            CommonPreferences.username = it.username
            CommonPreferences.userid = it.user_ID.toString()
            CommonPreferences.sex = it.sex
            CommonPreferences.age = it.age.toString()
            CommonPreferences.fans_num = it.fans_num.toString()
            CommonPreferences.signature = it.signature
            CommonPreferences.avatars = it.avatar
            CommonPreferences.week_hot_value = it.week_hot_value.toString()
            CommonPreferences.month_hot_value = it.month_hot_value.toString()
            CommonPreferences.year_hot_value = it.year_hot_value.toString()
            CommonPreferences.city = it.city
            CommonPreferences.rank = it.month_rank.toString()
            CommonPreferences.focus_num = it.follow_num.toString()
            CommonPreferences.tags = if (it.tags == null) "" else it.tags!!

            if (it.birthday != null) {
                CommonPreferences.birthday =
                    "${it.birthday!!.year}-${it.birthday!!.month}-${it.birthday!!.day}"
            } else {
                CommonPreferences.birthday = ""
            }


            callback(RefreshState.Success(AuthUtils.WECHAT_SUCCESS))
        }
    }

}

fun loginForQQ(qqOpenID: String, callback: suspend (RefreshState<String>) -> Unit = {}) {
    launch(UI) {
        AuthService.qqLogin(qqOpenID).awaitAndHandle {
            callback(RefreshState.Failure(it))
        }?.data?.let {
            CommonPreferences.token = it.token
            CommonPreferences.isQQ = true
            CommonPreferences.isLogin = true
            CommonPreferences.qq_openid = qqOpenID
            CommonPreferences.username = it.username
            CommonPreferences.userid = it.user_ID.toString()
            CommonPreferences.sex = it.sex
            CommonPreferences.age = it.age.toString()
            CommonPreferences.fans_num = it.fans_num.toString()
            CommonPreferences.signature = it.signature
            CommonPreferences.avatars = it.avatar
            CommonPreferences.week_hot_value = it.week_hot_value.toString()
            CommonPreferences.month_hot_value = it.month_hot_value.toString()
            CommonPreferences.year_hot_value = it.year_hot_value.toString()
            CommonPreferences.city = it.city
            CommonPreferences.rank = it.month_rank.toString()
            CommonPreferences.focus_num = it.follow_num.toString()
            CommonPreferences.tags = if (it.tags == null) "" else it.tags!!

            if (it.birthday != null) {
                CommonPreferences.birthday =
                    "${it.birthday!!.year}-${it.birthday!!.month}-${it.birthday!!.day}"
            } else {
                CommonPreferences.birthday = ""
            }


            callback(RefreshState.Success(AuthUtils.WECHAT_SUCCESS))
        }
    }

}
