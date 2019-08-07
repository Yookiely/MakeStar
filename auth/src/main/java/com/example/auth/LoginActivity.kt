package com.example.auth

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.auth.api.authSelfLiveData
import com.example.auth.api.login
import com.example.common.experimental.cache.CacheIndicator
import com.example.common.experimental.cache.RefreshState
import com.example.common.experimental.startActivity
import com.wingedvampires.homepage.view.HomePageActivity
import org.jetbrains.anko.coroutines.experimental.asReference

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameText : EditText
    private lateinit var passwordText : EditText
    private lateinit var loginButton: Button
    private lateinit var username : String
    private lateinit var passwords : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameText = findViewById(R.id.account_input)
        passwordText = findViewById(R.id.password_input)
        loginButton = findViewById<Button>(R.id.login_button).apply {
            setOnClickListener {
                hideSoftInputMethod()
                val activity = this@LoginActivity.asReference()
                if (usernameText.text.isBlank()){
                    Toast.makeText(this@LoginActivity, "请输入用户名", Toast.LENGTH_LONG).show()
                }else if (passwordText.text.isBlank()) {
                    Toast.makeText(this@LoginActivity, "请输入密码", Toast.LENGTH_LONG).show()
                }else {
                    this.isEnabled = false
                    login(usernameText.text.toString(), passwordText.text.toString()){
                        when (it) {
                            is RefreshState.Success ->
                                authSelfLiveData.refresh(CacheIndicator.REMOTE) {
                                    when (it) {
                                        is RefreshState.Success -> {
                                            Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_LONG).show()
//                                            startActivity(name = "welcome")
                                            var intent = Intent()
                                            intent.setClass(this@LoginActivity,HomePageActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        is RefreshState.Failure -> {
                                            Toast.makeText(this@LoginActivity, "发生错误 ${it.throwable.message}！${it.javaClass.name}", Toast.LENGTH_LONG).show()
                                            this.isEnabled = true
                                        }
                                    }
                                }
                            is RefreshState.Failure -> {
                                Toast.makeText(this@LoginActivity, "发生错误 ${it.throwable.message}！${it.javaClass.name}", Toast.LENGTH_LONG).show()
                                this.isEnabled = true
                            }
                        }
                    }
                }
            }

        }
        if (usernameText.text.toString()!=""&&passwordText.text.toString()!=""){
            username = usernameText.text.toString()
            passwords = passwordText.text.toString()
        }



    }

    private fun hideSoftInputMethod() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.apply {
            hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }
}
