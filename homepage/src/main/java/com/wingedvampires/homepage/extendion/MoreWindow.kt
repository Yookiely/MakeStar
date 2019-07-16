package com.wingedvampires.homepage.extendion

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.wingedvampires.homepage.R

class MoreWindow(internal var mContext: Activity) : PopupWindow(), View.OnClickListener {

    private val TAG = MoreWindow::class.java.simpleName
    internal lateinit var layout: RelativeLayout
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var statusBarHeight: Int = 0
    private var mBitmap: Bitmap? = null
    private var overlay: Bitmap? = null

    private val mHandler = Handler()

    fun init() {
        val frame = Rect()
        mContext.window.decorView.getWindowVisibleDisplayFrame(frame)
        statusBarHeight = frame.top
        val metrics = DisplayMetrics()
        mContext.windowManager.defaultDisplay
            .getMetrics(metrics)
        mWidth = metrics.widthPixels
        mHeight = metrics.heightPixels

        width = mWidth
        height = mHeight
    }

    private fun blur(): Bitmap? {
        if (null != overlay) {
            return overlay
        }
        val startMs = System.currentTimeMillis()

        val view = mContext.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        mBitmap = view.drawingCache

        val scaleFactor = 8f//ͼƬ���ű�����
        val radius = 10f//ģ���̶�
        val width = mBitmap!!.width
        val height = mBitmap!!.height

        overlay = Bitmap.createBitmap(
            (width / scaleFactor).toInt(),
            (height / scaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(overlay!!)
        canvas.scale(1 / scaleFactor, 1 / scaleFactor)
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(mBitmap!!, 0f, 0f, paint)

        overlay = FastBlur.doBlur(overlay!!, radius.toInt(), true)
        Log.i(TAG, "blur time is:" + (System.currentTimeMillis() - startMs))
        return overlay
    }

    private fun showAnimation1(view: View, fromY: Int, toY: Int): Animation {
        val set = AnimationSet(true)
        val go = TranslateAnimation(0f, 0f, fromY.toFloat(), toY.toFloat())
        go.duration = 300
        val go1 = TranslateAnimation(0f, 0f, -10f, 2f)
        go1.duration = 100
        go1.startOffset = 250
        set.addAnimation(go1)
        set.addAnimation(go)

        set.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationEnd(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationStart(animation: Animation) {

            }

        })
        return set
    }


    fun showMoreWindow(anchor: View, bottomMargin: Int) {
        layout = LayoutInflater.from(mContext).inflate(R.layout.center_music_more_window, null) as RelativeLayout
        contentView = layout

        val close = layout.findViewById(R.id.center_music_window_close) as ImageView
        //		android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //		params.bottomMargin = bottomMargin;
        //		params.addRule(RelativeLayout.BELOW, R.id.ll_2);
        //		params.addRule(RelativeLayout.RIGHT_OF, R.id.ll_1);
        //		View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
        //		ImageView standrad  = view.findViewById(R.id.show);
        //		params.height = standrad.getHeight();
        //		params.width = standrad.getWidth();
        //		params.topMargin = 200;
        //		params.leftMargin = 18;
        //		close.setLayoutParams(params);

        close.setOnClickListener {
            if (isShowing) {
                closeAnimation(layout)
            }
        }

        layout.setOnClickListener { v ->
            when (v.id) {
                R.id.more_window_auto -> dismiss()
                R.id.more_window_external -> {
                }

                else -> {
                }
            }
            if (isShowing) {
                closeAnimation(layout)
            }
        }

        showAnimation(layout)
        //setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        isOutsideTouchable = true
        isFocusable = true
        width = RelativeLayout.LayoutParams.MATCH_PARENT
        height = RelativeLayout.LayoutParams.MATCH_PARENT
        showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight)
    }

    private fun showAnimation(layout: ViewGroup) {
        for (i in 0 until layout.childCount - 1) {
            val child = layout.getChildAt(i)
            if (child.id == R.id.center_music_window_close) {
                continue
            }
            child.setOnClickListener(this)
            child.visibility = View.INVISIBLE
            mHandler.postDelayed({
                child.visibility = View.VISIBLE
                val fadeAnim: ObjectAnimator = ObjectAnimator.ofFloat(child, "translationY", 600F, 0F)
                fadeAnim.duration = 300
                val kickAnimator = KickBackAnimator()
                kickAnimator.setDuration(150)
                fadeAnim.setEvaluator(kickAnimator)
                fadeAnim.start()
            }, (i * 50).toLong())
        }

    }

    private fun closeAnimation(layout: ViewGroup) {
        for (i in 0 until layout.childCount - 1) {
            val child = layout.getChildAt(i)
            if (child.id == R.id.center_music_window_close) {
                continue
            }
            child.setOnClickListener(this)
            mHandler.postDelayed({
                child.visibility = View.VISIBLE
                val fadeAnim: ObjectAnimator = ObjectAnimator.ofFloat(child, "translationY", 0.0F, 600.0F)
                fadeAnim.duration = 200
                val kickAnimator = KickBackAnimator()
                kickAnimator.setDuration(100)
                fadeAnim.setEvaluator(kickAnimator)
                fadeAnim.start()
                fadeAnim.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {
                        // TODO Auto-generated method stub

                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        // TODO Auto-generated method stub

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        child.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        // TODO Auto-generated method stub

                    }
                })
            }, ((layout.childCount - i - 1) * 30).toLong())

            if (child.id == R.id.ll_2) {
                mHandler.postDelayed({ dismiss() }, ((layout.childCount - i) * 30 + 80).toLong())
            }
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.more_window_auto -> dismiss()
            R.id.more_window_external -> {
            }

            else -> {
            }
        }
        if (isShowing) {
            closeAnimation(layout)
        }
    }

    fun destroy() {
        if (null != overlay) {
            overlay!!.recycle()
            overlay = null
            System.gc()
        }
        if (null != mBitmap) {
            mBitmap!!.recycle()
            mBitmap = null
            System.gc()
        }
    }

}
