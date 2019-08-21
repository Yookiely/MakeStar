package com.wingedvampires.attention.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.wingedvampires.attention.R

class CommentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_comments)
    }
}
