package com.aliyun.apsaravideo.recorder.view.focus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.aliyun.apsaravideo.R;

/**
 * 垂直seekbar
 *
 * @author xlx
 */
public class AlivcVerticalSeekBar extends View {

    private Paint mPaint;
    private Drawable mThumbDrawable;

    private Rect mThumbLocationRect = new Rect();
    private Rect mProgressRect = new Rect();

    private static final int PROGRESS_MIN_WIDTH = 80;
    private static final float PROGRESS_MIN_HEIGHT = 200;
    private static final int THUMB_MAX_TOP = 170;
    private static final int THUMB_MIN_BOTTOM = 30;
    private int viewWidth;
    private int viewHeight;
    private float mProgress;
    private int mHalfBitmapWidth;
    private int mHalfBitmapHeigth;
    private int thumbHeight;

    public AlivcVerticalSeekBar(Context context) {
        this(context, null);
    }

    public AlivcVerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlivcVerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThumbResource(context);
        initPaint();
    }

    private void initThumbResource(Context context) {
        mThumbDrawable = context.getResources().getDrawable(R.drawable.alivc_record_light_sun);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;
        // 进度条尺寸
        mProgressRect.left = w >> 1 - 4;
        mProgressRect.top = 0;
        mProgressRect.right = w >> 1 + 4;
        mProgressRect.bottom = h;

        mHalfBitmapWidth = mThumbDrawable.getIntrinsicWidth() >> 1;
        mHalfBitmapHeigth = mThumbDrawable.getIntrinsicHeight() >> 1;



        int thumbWidth = mHalfBitmapWidth >> 2;
        thumbHeight = mHalfBitmapHeigth >> 1;
        mThumbLocationRect = new Rect(-thumbWidth, 0, thumbWidth, thumbHeight);
        setProgress(mProgress, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(viewWidth >> 1, 0);
        canvas.drawRect(mProgressRect, mPaint);
        mThumbDrawable.setBounds(mThumbLocationRect);
        mThumbDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result = PROGRESS_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                break;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        float result = PROGRESS_MIN_HEIGHT;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                break;
        }
        return (int)result;
    }

    public void setProgress(float progress, boolean isDown) {
        this.mProgress = progress;
        progress = 1.0f - progress;
       int  pro = (int)(viewHeight * progress - thumbHeight);
        if (isDown) {
            mThumbLocationRect.top = pro;
            mThumbLocationRect.bottom = pro + thumbHeight;
            // 向上滑动
        } else {
            // 向下滑动
            mThumbLocationRect.top = pro;
            mThumbLocationRect.bottom = pro + thumbHeight;
        }

        // 下边界
        if (mThumbLocationRect.bottom > PROGRESS_MIN_HEIGHT && mThumbLocationRect.top > THUMB_MAX_TOP) {
            mThumbLocationRect.bottom = viewHeight;
            mThumbLocationRect.top = THUMB_MAX_TOP;
        }
        // 上边界
        if (mThumbLocationRect.top < 0 && mThumbLocationRect.bottom < THUMB_MIN_BOTTOM) {
            mThumbLocationRect.top = 0;
            mThumbLocationRect.bottom = THUMB_MIN_BOTTOM;
        }
        postInvalidate();
    }
}
