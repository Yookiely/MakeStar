package com.yookie.upload

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.upload.service.UploadService
import kotlinx.android.synthetic.main.activity_success.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_success)
        var back = findViewById<TextView>(R.id.back_home)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        val workID = intent.getStringExtra("videoId")
        if (workID.isNotEmpty()){
            launch(UI+ QuietCoroutineExceptionHandler){
                val callback = UploadService.workUploadAck(workID).await()
                if (callback.error_code==-1){
                    textit.text = "发送成功！"
                }
            }

        }
        back.setOnClickListener {
            Transfer.startActivity(this,"HomePageActivity", Intent())
        }
    }
}
