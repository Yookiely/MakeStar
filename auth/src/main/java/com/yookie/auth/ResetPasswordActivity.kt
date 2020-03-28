package com.yookie.auth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_reset_password)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        val key = intent.getStringExtra("key")
        val username = intent.getStringExtra("username")
        forget_finish_logon.setOnClickListener {

            if (forget_password.text.isNotEmpty()&&forget_password_re.text.isNotEmpty()){
                val password = forget_password.text.toString()
                val repassword = forget_password_re.text.toString()
                if (password==repassword){
                    launch(UI+ QuietCoroutineExceptionHandler){
                        val callback = AuthService.updatePassword(username,key,password).await()
                        when(callback.message){
                            "succeed" -> startActivity(Intent(this@ResetPasswordActivity,LoginActivity::class.java))
                            "已超时" -> Toast.makeText(this@ResetPasswordActivity,"设置时常已超时，请重新输入密保问题", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(this@ResetPasswordActivity,"未知错误，请重试", Toast.LENGTH_SHORT).show()

                        }


                    }
                }else{
                    Toast.makeText(this,"两次密码请一致", Toast.LENGTH_SHORT).show()
                }


            }else{
                Toast.makeText(this,"请填写完整", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
