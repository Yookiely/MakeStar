package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yookie.auth.api.authSelfLiveData
import com.yookie.common.experimental.extensions.bindNonNull
import kotlinx.android.synthetic.main.activity_fans.*

class FansActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fans)

        authSelfLiveData.bindNonNull(this) {
            Glide.with(this)
                .load(it.data?.avatar)
                .into(fans_user_portrait)
            fans_user_nickname.text = it.data?.username

        }
      }
}
