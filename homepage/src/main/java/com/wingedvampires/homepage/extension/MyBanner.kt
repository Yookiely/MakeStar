package com.wingedvampires.homepage.extension

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.youth.banner.Banner

// 为了解决Banner和viewpager的滑动冲突
class MyBanner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    Banner(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(event)
    }
}