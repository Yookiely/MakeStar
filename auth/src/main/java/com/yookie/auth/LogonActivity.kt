package com.yookie.auth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class LogonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logon)
        var username = findViewById<EditText>(R.id.logon_input)
        var password = findViewById<EditText>(R.id.password_input)
        var rePassword = findViewById<EditText>(R.id.re_password_input)
        var start = findViewById<Button>(R.id.start_logon)
        start.setOnClickListener {
            if (username.text!=null&&password.text!=null&&rePassword.text!=null){
                if (password.text.toString()==rePassword.text.toString()){
                    launch(UI + QuietCoroutineExceptionHandler){
                        var callback = AuthService.register(username.text.toString(),password.text.toString()).await()
                        if (callback.error_code==-1){
                            Log.d("zhucechenggong","注册成功")
                        }
                    }
                }else {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "信息请填写完整", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
