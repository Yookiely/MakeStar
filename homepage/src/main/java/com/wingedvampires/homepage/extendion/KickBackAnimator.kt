package com.wingedvampires.homepage.extendion

import android.animation.TypeEvaluator

class KickBackAnimator : TypeEvaluator<Float> {
    private val s = 1.70158f
    private var mDuration = 0f

    fun setDuration(duration: Int) {
        mDuration = duration.toFloat()
    }

    override fun evaluate(fraction: Float, startValue: Float?, endValue: Float?): Float? {
        val t = mDuration * fraction
        val b = startValue!!.toFloat()
        val c = endValue!!.toFloat() - startValue.toFloat()
        val d = mDuration
        return calculate(t, b, c, d)!!
    }

    fun calculate(t: Float, b: Float, c: Float, d: Float): Float? {
        val t = t / d - 1
        return c * ((t) * t * ((s + 1) * t + s) + 1) + b
    }
}