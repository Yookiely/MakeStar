package com.yookie.auth

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.Tencent
import com.yookie.auth.api.*
import com.yookie.common.AuthService
import com.yookie.common.WeiXin
import com.yookie.common.WeiXinService
import com.yookie.common.experimental.CommonContext
import com.yookie.common.experimental.CommonContext.QQ_APPID
import com.yookie.common.experimental.cache.CacheIndicator.REMOTE
import com.yookie.common.experimental.cache.RefreshState
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.coroutines.experimental.asReference
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: TextView
    private lateinit var forgetButton: TextView
    private lateinit var weiXinButton: ImageView
    private lateinit var qqButton: ImageView
    private lateinit var username: String
    private lateinit var passwords: String
    private lateinit var wxAPI: IWXAPI
    private lateinit var logon: TextView
    private lateinit var context: Context
    private lateinit var listener: BaseUiListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)
        listener = BaseUiListener()
        // 注册EventBus
        EventBus.getDefault().register(this)//注册
        wxAPI = WXAPIFactory.createWXAPI(this, CommonContext.WECHAT_APPID, true)
        val localLayoutParams = window.attributes
        localLayoutParams.flags =
            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        wxAPI.registerApp(CommonContext.WECHAT_APPID)
        usernameText = findViewById(R.id.account_input)
        passwordText = findViewById(R.id.password_input)
        qqButton = findViewById(R.id.qq_button)
        weiXinButton = findViewById(R.id.we_button)
        forgetButton = findViewById(R.id.forget_password)
        val rule = findViewById<TextView>(R.id.rule)
        val agreement = findViewById<TextView>(R.id.agreement)
        logon = findViewById(R.id.logup)
        passwordText.inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
        logon.setOnClickListener {
            startActivity(Intent(this, LogonActivity::class.java))
        }
        forgetButton.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
        }
        loginButton = findViewById<TextView>(R.id.login_button).apply {
            setOnClickListener {
                if (agree_check.isChecked) {
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
                                    authSelfLiveData.refresh(REMOTE) {
                                        when (it) {
                                            is RefreshState.Success -> {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "登录成功",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                Transfer.startActivity(
                                                    this@LoginActivity,
                                                    "HomePageActivity",
                                                    Intent()
                                                )
                                                finish()
                                            }
                                            is RefreshState.Failure -> {
                                            }
                                        }
                                    }
//                                Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_LONG).show()
//                                Transfer.startActivity(
//                                    this@LoginActivity,
//                                    "HomePageActivity",
//                                    Intent()
//                                )
                                }

                                is RefreshState.Failure -> {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "请检查用户名密码是否错误",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    this.isEnabled = true
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "请先同意协议", Toast.LENGTH_SHORT).show()
                }

            }
        }
        weiXinButton.setOnClickListener {
            if (agree_check.isChecked) {
                login()
            } else {
                Toast.makeText(this, "请先同意协议", Toast.LENGTH_SHORT).show()
            }
        }
        qqButton.setOnClickListener {

        }
        rule.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        agreement.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        rule.setOnClickListener {
            val mIntent = Intent()
            mIntent.putExtra("FLAG", "rule")
            Transfer.startActivityWithoutClose(this, "AgreementActivity", mIntent)
        }
        agreement.setOnClickListener {
            val mIntent = Intent()
            mIntent.putExtra("FLAG", "agreement")
            Transfer.startActivityWithoutClose(this, "AgreementActivity", mIntent)
        }


    }

    private fun qqLogin() {
        val mTencent = Tencent.createInstance(QQ_APPID, this.applicationContext)
        if (!mTencent.isSessionValid) {
//            mTencent.login(this, "all", object : IUiListener {
////                override fun onComplete(p0: Any?) {
////                    val token = mTencent.qqToken
////                    val openID =mTencent.openId
////                    Log.d("log_openid",openID)
////                    Toast.makeText(context, "授权成功", Toast.LENGTH_SHORT).show()
////                }
////
////                override fun onCancel() {
////                }
////
////                override fun onError(p0: UiError?) {
////                }
////
////            },true)
            mTencent.login(this, "all", listener)
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

                    val weiXinInfo = WeiXinService.getUserInfo(this.access_token!!, this.openid!!)
                        .awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT)
                                .show()
                        }

                    if (weiXinInfo?.openid != null) {
                        val isWechat = AuthService.isWechat(weiXinInfo.openid!!).awaitAndHandle {
                            it.printStackTrace()
                        }?.data?.message

                        if (isWechat == 1) {
                            loginForWechat(weiXinInfo.openid!!) {
                                when (it) {
                                    is RefreshState.Success -> {
                                        when (it.message) {
                                            AuthUtils.WECHAT_SUCCESS -> {
                                                authSelfLiveData.refresh(REMOTE) {
                                                    when (it) {
                                                        is RefreshState.Success -> {
                                                            Toast.makeText(
                                                                this@LoginActivity,
                                                                "登录成功",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            Transfer.startActivity(
                                                                this@LoginActivity,
                                                                "HomePageActivity",
                                                                Intent()
                                                            )

                                                            finish()
                                                        }
                                                        is RefreshState.Failure -> {
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    is RefreshState.Failure -> {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "微信登录有误，请尝试账号登录",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            val intent = Intent(
                                this@LoginActivity,
                                LogonActivity::class.java
                            )
                            intent.putExtra(
                                AuthUtils.WECHAT_REGISTER,
                                weiXinInfo.openid!!
                            )
                            startActivity(intent)
                        }


                    } else {
                        Toast.makeText(this@LoginActivity, "微信登录失败", Toast.LENGTH_SHORT).show()
                    }
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

    @Subscribe
    fun onEventMainThread(response: Object) {
        val openId = (response as JSONObject).getString("openid")

        launch(UI + QuietCoroutineExceptionHandler) {
            val isQQ = AuthService.isQQ(openId).awaitAndHandle {
                it.printStackTrace()
            }?.data?.message

            if (isQQ == 1) {
                loginForQQ(openId) {
                    when (it) {
                        is RefreshState.Success -> {
                            when (it.message) {
                                AuthUtils.WECHAT_SUCCESS -> {
                                    authSelfLiveData.refresh(REMOTE) {
                                        when (it) {
                                            is RefreshState.Success -> {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "登录成功",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                Transfer.startActivity(
                                                    this@LoginActivity,
                                                    "HomePageActivity",
                                                    Intent()
                                                )

                                                finish()
                                            }
                                            is RefreshState.Failure -> {
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        is RefreshState.Failure -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "QQ登录有误，请尝试账号登录",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                val intent = Intent(
                    this@LoginActivity,
                    LogonActivity::class.java
                )
                intent.putExtra(
                    AuthUtils.QQ_REGISTER,
                    openId
                )
                startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Tencent.onActivityResultData(requestCode, resultCode, data, listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)//取消注册
    }
}
