package com.yookie.auth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class QuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_question)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        val token = intent.getStringExtra("token")
        CommonPreferences.token = token

        finish_logon.setOnClickListener {
            val question = question_input.text.toString()
            val answer = answer_input.text.toString()
            if(question.isNotEmpty()&& answer.isNotEmpty()) {
                launch(UI + QuietCoroutineExceptionHandler) {
                    val callback = AuthService.setQusetion(question, answer).await()
                    if (callback.message == "succeed") {
                        Toast.makeText(this@QuestionActivity, "注册成功", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@QuestionActivity, LoginActivity::class.java))

                    }

                }
            }else{
                Toast.makeText(this@QuestionActivity, "问题和答案不能为空", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
