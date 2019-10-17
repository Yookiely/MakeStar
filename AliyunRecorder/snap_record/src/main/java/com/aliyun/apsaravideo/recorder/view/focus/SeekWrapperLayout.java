package com.aliyun.apsaravideo.recorder.view.focus;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aliyun.apsaravideo.R;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * 曝光度layout
 *
 * @author xlx
 */
public class SeekWrapperLayout extends RelativeLayout {

    /**
     * Handler message code
     */
    private static final int MSG_HIDE_VIEW = 1000;

    /**
     * 延迟隐藏时间
     */
    private static final int DELAYED_HIDE_DURATION = 2700;

    private TimeHandler mTimeHandler;
    private AlivcVerticalSeekBar mVerticalSeekBar;

    private int mViewHeight;
    private int mViewWidth;
    private int mTotalWidth;
    private int mTotalHeight;
    private ImageView mFocusView;
    private OnViewHideListener mOnViewHideListener;

    public SeekWrapperLayout(Context context) {
        this(context, null);
    }

    public SeekWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_wrapper, this, true);
        mVerticalSeekBar = findViewById(R.id.vertical_seekbar);
        mFocusView = findViewById(R.id.iv_focus);
    }

    public void setProgress(float scrollValue, boolean isDown) {
        mVerticalSeekBar.setProgress(scrollValue, isDown);
    }

    public void setProgress(float scrollValue) {
        this.setProgress(scrollValue, false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    /**
     * 设置view的显示坐标
     * @param x x方向
     * @param y y方向
     */
    public void setLocation(float x, float y) {
        float offsetX = x - (getMeasuredWidth() >> 1);
        float offsetY = y - (getMeasuredHeight() >> 8);
        setX(offsetX);
        setY(offsetY);
        // 如果绘制位置大于总宽度的 1/2, 翻转view, 防止seek在边界时绘制不全
        int halfWidth  = mTotalWidth >> 1;
        if (offsetX > halfWidth) {
            setRotationY(180);
        } else {
            setRotationY(0);
        }
    }

    public void showView() {
        if (mTimeHandler == null) {
            mTimeHandler = new TimeHandler(this);
        }
        int visibility = getVisibility();
        if (visibility == VISIBLE) {
            mTimeHandler.removeMessages(MSG_HIDE_VIEW);
        } else {
            setVisibility(VISIBLE);
        }
        mTimeHandler.sendMessageDelayed(mTimeHandler.obtainMessage(MSG_HIDE_VIEW), DELAYED_HIDE_DURATION);
    }

    public void activityStop() {
        setVisibility(GONE);
        if (mTimeHandler != null) {
            if (mTimeHandler.weakReference != null) {
                mTimeHandler.weakReference.clear();
            }
            mTimeHandler.removeMessages(MSG_HIDE_VIEW);
            mTimeHandler = null;
        }
    }

    public void setDrawingSize(int width, int height) {
        this.mTotalWidth = width;
        this.mTotalHeight = height;
    }

    public void setOnViewHideListener(OnViewHideListener listener) {
        this.mOnViewHideListener = listener;
    }

    static class TimeHandler extends Handler {
        WeakReference<SeekWrapperLayout> weakReference;

        TimeHandler(SeekWrapperLayout wrapperLayout) {
            weakReference = new WeakReference<>(wrapperLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDE_VIEW) {
                SeekWrapperLayout seekWrapperLayout = weakReference.get();
                seekWrapperLayout.hideView();
            }
        }
    }

    private void hideView() {
        setVisibility(GONE);
        if (mOnViewHideListener != null) {
            mOnViewHideListener.onHided();
        }
    }

    public interface OnViewHideListener {
        void onHided();
    }
}
