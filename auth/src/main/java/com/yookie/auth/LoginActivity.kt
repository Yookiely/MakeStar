package com.yookie.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yookie.auth.api.authSelfLiveData
import com.yookie.auth.api.login
import com.yookie.common.WeiXin
import com.yookie.common.WeiXinService
import com.yookie.common.experimental.CommonContext
import com.yookie.common.experimental.cache.CacheIndicator.REMOTE
import com.yookie.common.experimental.cache.RefreshState
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.coroutines.experimental.asReference

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var weiXinButton: Button
    private lateinit var username: String
    private lateinit var passwords: String
    private lateinit var wxAPI: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 注册EventBus
        EventBus.getDefault().register(this)//注册
        wxAPI = WXAPIFactory.createWXAPI(this, CommonContext.WECHAT_APPID, true)
        wxAPI.registerApp(CommonContext.WECHAT_APPID)

        usernameText = findViewById(R.id.account_input)
        passwordText = findViewById(R.id.password_input)
        loginButton = findViewById<Button>(R.id.login_button).apply {
            setOnClickListener {
                hideSoftInputMethod()
                val activity = this@LoginActivity.asReference()
                if (usernameText.text.isBlank()) {
                    Toast.makeText(this@LoginActivity, "请输入用户名", Toast.LENGTH_LONG).show()
                } else if (passwordText.text.isBlank()) {
                    Toast.makeText(this@LoginActivity, "请输入密码", Toast.LENGTH_LONG).show()
                } else {
                    this.isEnabled = false
                    login(usernameText.text.toString(), passwordText.text.toString()) {
                        when (it) {
                            is RefreshState.Success -> {
                                authSelfLiveData.refresh(REMOTE)
                                Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_LONG).show()
                                Transfer.startActivity(
                                    this@LoginActivity,
                                    "HomePageActivity",
                                    Intent()
                                )
                            }

                            is RefreshState.Failure -> {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "发生错误 ${it.throwable.message}！${it.javaClass.name}",
                                    Toast.LENGTH_LONG
                                ).show()
                                this.isEnabled = true
                            }
                        }
                    }
                }
            }

        }
        weiXinButton = findViewById<Button>(R.id.weixin_button).apply {
            setOnClickListener { login() }

        }

    }

    private fun login() {
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = System.currentTimeMillis().toString()
        wxAPI.sendReq(req)
    }

    /**
     * 这里用到的了EventBus框架
     * 博客教程:http://blog.csdn.net/lmj623565791/article/details/40920453
     * @param weiXin
     */
    @Subscribe
    fun onEventMainThread(weiXin: WeiXin) {
        Log.i("ansen", "收到eventbus请求 type:" + weiXin.type)
        if (weiXin.type == 1) {//登录

            launch(UI + QuietCoroutineExceptionHandler) {
                val weiXinToken = WeiXinService.getAccessToken(weiXin.code!!).awaitAndHandle {
                    it.printStackTrace()
                    Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
                }?.apply {

                    val weiXinInfo =
                        WeiXinService.getUserInfo(this.access_token!!, this.openid!!)
                            .awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    usernameText.setText(weiXinInfo?.nickname)
                    Toast.makeText(this@LoginActivity, weiXinInfo?.nickname, Toast.LENGTH_SHORT)
                        .show()
                }

            }
            //getAccessToken(weiXin.code)
        } else if (weiXin.type == 2) {//分享
            when (weiXin.errCode) {
                BaseResp.ErrCode.ERR_OK -> Log.i("ansen", "微信分享成功.....")
                BaseResp.ErrCode.ERR_USER_CANCEL//分享取消
                -> Log.i("ansen", "微信分享取消.....")
                BaseResp.ErrCode.ERR_AUTH_DENIED//分享被拒绝
                -> Log.i("ansen", "微信分享被拒绝.....")
            }
        } else if (weiXin.type == 3) {//微信支付
            if (weiXin.errCode == BaseResp.ErrCode.ERR_OK) {//成功
                Log.i("ansen", "微信支付成功.....")
            }
        }
    }

    private fun hideSoftInputMethod() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.apply {
            hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)//取消注册
    }
}
