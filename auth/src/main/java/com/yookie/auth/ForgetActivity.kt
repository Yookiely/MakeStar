package com.yookie.auth

import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.android.synthetic.main.activity_forget.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ForgetActivity : AppCompatActivity() {
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_forget)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        forget_start_logon.setOnClickListener {
            username = forget_logon_input.text.toString()
            launch(UI+ QuietCoroutineExceptionHandler){
                val callback = AuthService.getQuestion(username).await()
                if (callback.message=="succeed"){
                    forget_start_logon.visibility = View.INVISIBLE
                    forget_logon_input.visibility = View.INVISIBLE
                    forget_middle_logon.visibility = View.VISIBLE
                    question.visibility = View.VISIBLE
                    question_text.visibility = View.VISIBLE
                    answer_text.visibility = View.VISIBLE
                    question.text = callback.data?.question


                }else{
                    Toast.makeText(this@ForgetActivity, "请检查用户名是否正确", Toast.LENGTH_SHORT).show()
                }
            }
        }
        forget_middle_logon.setOnClickListener {
            val answer = answer_text.text.toString()
            launch(UI + QuietCoroutineExceptionHandler) {
                val callback = AuthService.getKeyByQuestion(username, answer).await()
                when (callback.message) {
                    "succeed" -> {
                        val intent = Intent(this@ForgetActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("key", callback.data?.result)
                        intent.putExtra("username",username)
                        startActivity(intent)
                    }
                    "密保答案错误" -> {
                        Toast.makeText(this@ForgetActivity, "密保答案错误", Toast.LENGTH_SHORT).show()
                    }

                    else -> Toast.makeText(
                        this@ForgetActivity,
                        "网络故障，请稍后再试",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
        }




    }
}
