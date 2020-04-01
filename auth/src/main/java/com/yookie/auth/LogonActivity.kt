package com.yookie.auth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.yookie.auth.api.AuthUtils
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class LogonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_logon)
        var username = findViewById<EditText>(R.id.logon_input)
        var password = findViewById<EditText>(R.id.password_input)
        var rePassword = findViewById<EditText>(R.id.re_password_input)
        var start = findViewById<TextView>(R.id.start_logon)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)

        val weiXinOpenID: String? = intent.getStringExtra(AuthUtils.WECHAT_REGISTER)
        val qqOpenID: String? = intent.getStringExtra(AuthUtils.QQ_REGISTER)

        password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        rePassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        start.setOnClickListener {
            if (username.text != null && password.text != null && rePassword.text != null) {
                if (password.text.toString() == rePassword.text.toString()) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        var callback =
                            AuthService.register(
                                username.text.toString(),
                                password.text.toString(),
                                wechat = weiXinOpenID,
                                qq = qqOpenID
                            )
                                .await()
                        if (callback.error_code == -1) {
                            Log.d("zhucechenggong", "注册成功")
                            val intent = Intent(this@LogonActivity, QuestionActivity::class.java)
                            intent.putExtra("token", callback.data?.token)
                            startActivity(intent)

                        }
                        if (callback.error_code == 1) {
                            Toast.makeText(this@LogonActivity, "注册失败", Toast.LENGTH_SHORT).show()
                        }
                        if (callback.error_code == 2) {
                            Toast.makeText(this@LogonActivity, "用户已存在", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "信息请填写完整", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
