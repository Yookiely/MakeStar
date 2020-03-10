package com.example.yangzihang.makestar

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.InputDevice
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_my_user.view.*

class LeaveMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_message)
        val toAvatars =findViewById<ImageView>(R.id.leave_to_portrait)
        val fromAavatars = findViewById<ImageView>(R.id.leave_from_portrait)
        val toName = findViewById<TextView>(R.id.leave_to_name)
        val fromName = findViewById<TextView>(R.id.leave_from_name)
        val hintText = findViewById<TextView>(R.id.leave_hint)
        val editText = findViewById<EditText>(R.id.leave_ms)
        val sendTips  =findViewById<TextView>(R.id.leave_send_ms)
        val send  =findViewById<Button>(R.id.leave_send)
        val userid = intent.getStringExtra("userID")
        UserImp.getUserInfo(userid){
            Glide.with(this)
                .load(it.avatar)
                .into(toAvatars)
            toName.text = it.username
        }
        sendTips.visibility = View.INVISIBLE
        fromName.text = CommonPreferences.username
        Glide.with(this)
            .load(CommonPreferences.avatars)
            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
            .into(fromAavatars)
        hintText.setOnClickListener {
            hintText.visibility= View.INVISIBLE
            editText.isCursorVisible =true
            editText.requestFocus()
            showSoftKeyboard(editText)
        }
        send.setOnClickListener {
            UserImp.sendMessage(CommonPreferences.userid,userid,editText.text.toString(),CommonPreferences.userid) {
                Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            sendTips.visibility = View.VISIBLE
        }
    }
    fun showSoftKeyboard(view: View){
        var imm:InputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED)
    }
}
