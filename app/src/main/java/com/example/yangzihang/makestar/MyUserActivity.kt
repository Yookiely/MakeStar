package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.auth.api.authSelfLiveData
import com.example.common.experimental.extensions.bindNonNull

class MyUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_my_user)
        var portrait = findViewById<ImageView>(R.id.portrait)
        authSelfLiveData.bindNonNull(this){
            Glide.with(this)
                .load(it.data?.avatar)
                .into(portrait)
        }
    }
}
