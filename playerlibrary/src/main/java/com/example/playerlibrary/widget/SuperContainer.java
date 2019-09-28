/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.playerlibrary.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.playerlibrary.event.EventDispatcher;
import com.example.playerlibrary.event.IEventDispatcher;
import com.example.playerlibrary.extension.BaseEventProducer;
import com.example.playerlibrary.extension.DelegateReceiverEventSender;
import com.example.playerlibrary.extension.IProducerGroup;
import com.example.playerlibrary.extension.ProducerEventSender;
import com.example.playerlibrary.extension.ProducerGroup;
import com.example.playerlibrary.log.PLog;
import com.example.playerlibrary.receiver.BaseCover;
import com.example.playerlibrary.receiver.CoverComparator;
import com.example.playerlibrary.receiver.DefaultLevelCoverContainer;
import com.example.playerlibrary.receiver.ICoverStrategy;
import com.example.playerlibrary.receiver.IReceiver;
import com.example.playerlibrary.receiver.IReceiverGroup;
import com.example.playerlibrary.receiver.OnReceiverEventListener;
import com.example.playerlibrary.receiver.StateGetter;
import com.example.playerlibrary.touch.BaseGestureCallbackHandler;
import com.example.playerlibrary.touch.ContainerTouchHelper;
import com.example.playerlibrary.touch.OnTouchGestureListener;


/**
 * Created by Taurus on 2018/3/17.
 */

public class SuperContainer extends FrameLayout implements OnTouchGestureListener {

    final String TAG = "SuperContainer";

    private FrameLayout mRenderContainer;
    private ICoverStrategy mCoverStrategy;

    private IReceiverGroup mReceiverGroup;
    private IEventDispatcher mEventDispatcher;

    private OnReceiverEventListener mOnReceiverEventListener;
    private ContainerTouchHelper mTouchHelper;

    private IProducerGroup mProducerGroup;

    private StateGetter mStateGetter;
    private DelegateReceiverEventSender mDelegateReceiverEventSender =
            new DelegateReceiverEventSender() {
                @Override
                public void sendEvent(int eventCode, Bundle bundle,
                                      IReceiverGroup.OnReceiverFilter receiverFilter) {
                    if (mEventDispatcher != null)
                        mEventDispatcher.dispatchProducerEvent(eventCode, bundle, receiverFilter);
                }

                @Override
                public void sendObject(String key, Object value, IReceiverGroup.OnReceiverFilter receiverFilter) {
                    if (mEventDispatcher != null)
                        mEventDispatcher.dispatchProducerData(key, value, receiverFilter);
                }
            };
    //receiver event listener, a bridge for some receivers communication.
    private OnReceiverEventListener mInternalReceiverEventListener =
            new OnReceiverEventListener() {
                @Override
                public void onReceiverEvent(int eventCode, Bundle bundle) {
                    if (mOnReceiverEventListener != null)
                        mOnReceiverEventListener.onReceiverEvent(eventCode, bundle);
                    if (mEventDispatcher != null)
                        mEventDispatcher.dispatchReceiverEvent(eventCode, bundle);
                }
            };
    //dynamic attach a receiver when user add it
    //detach a receiver when user remove it.
    private IReceiverGroup.OnReceiverGroupChangeListener mInternalReceiverGroupChangeListener =
            new IReceiverGroup.OnReceiverGroupChangeListener() {
                @Override
                public void onReceiverAdd(String key, IReceiver receiver) {
                    attachReceiver(receiver);
                }

                @Override
                public void onReceiverRemove(String key, IReceiver receiver) {
                    detachReceiver(receiver);
                }
            };

    public SuperContainer(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        initBaseInfo(context);
        initGesture(context);
        initRenderContainer(context);
        initReceiverContainer(context);
    }

    private void initBaseInfo(Context context) {
        mProducerGroup = new ProducerGroup(new ProducerEventSender(mDelegateReceiverEventSender));
    }

    protected void initGesture(Context context) {
        mTouchHelper = new ContainerTouchHelper(context, getGestureCallBackHandler());
        setGestureEnable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchHelper.onTouch(event);
    }

    protected BaseGestureCallbackHandler getGestureCallBackHandler() {
        return new BaseGestureCallbackHandler(this);
    }

    public void setGestureEnable(boolean enable) {
        mTouchHelper.setGestureEnable(enable);
    }

    public void setGestureScrollEnable(boolean enable) {
        mTouchHelper.setGestureScrollEnable(enable);
    }

    private void initReceiverContainer(Context context) {
        mCoverStrategy = getCoverStrategy(context);
        addView(mCoverStrategy.getContainerView(),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

    protected ICoverStrategy getCoverStrategy(Context context) {
        return new DefaultLevelCoverContainer(context);
    }

    private void initRenderContainer(Context context) {
        mRenderContainer = new FrameLayout(context);
        addView(mRenderContainer,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public final void setRenderView(View view) {
        removeRender();
        //must set WRAP_CONTENT and CENTER for render aspect ratio and measure.
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        mRenderContainer.addView(view, lp);
    }

    public final void setStateGetter(StateGetter stateGetter) {
        mStateGetter = stateGetter;
    }

    public final void dispatchPlayEvent(int eventCode, Bundle bundle) {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchPlayEvent(eventCode, bundle);
    }

    public final void dispatchErrorEvent(int eventCode, Bundle bundle) {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchErrorEvent(eventCode, bundle);
    }

    public void setOnReceiverEventListener(OnReceiverEventListener onReceiverEventListener) {
        this.mOnReceiverEventListener = onReceiverEventListener;
    }

    /**
     * add a event producer by yourself custom.
     *
     * @param eventProducer
     */
    public void addEventProducer(BaseEventProducer eventProducer) {
        mProducerGroup.addEventProducer(eventProducer);
    }

    /**
     * remove event producer.
     *
     * @param eventProducer
     * @return
     */
    public boolean removeEventProducer(BaseEventProducer eventProducer) {
        return mProducerGroup.removeEventProducer(eventProducer);
    }

    public final void setReceiverGroup(IReceiverGroup receiverGroup) {
        if (receiverGroup == null
                || receiverGroup.equals(mReceiverGroup))
            return;
        //remove all old covers from root container.
        removeAllCovers();

        //clear listener
        if (mReceiverGroup != null) {
            mReceiverGroup.removeOnReceiverGroupChangeListener(mInternalReceiverGroupChangeListener);
        }

        this.mReceiverGroup = receiverGroup;
        //init event dispatcher.
        mEventDispatcher = new EventDispatcher(receiverGroup);

        //sort it by CoverLevel
        mReceiverGroup.sort(new CoverComparator());

        //loop attach receivers
        mReceiverGroup.forEach(new IReceiverGroup.OnLoopListener() {
            @Override
            public void onEach(IReceiver receiver) {
                attachReceiver(receiver);
            }
        });
        //add a receiver group change listener, dynamic attach a receiver
        // when user add it or detach a receiver when user remove it.
        mReceiverGroup.addOnReceiverGroupChangeListener(mInternalReceiverGroupChangeListener);
    }

    //attach receiver, bind receiver event listener
    // and add cover container if it is a cover instance.
    private void attachReceiver(IReceiver receiver) {
        //bind the ReceiverEventListener for receivers connect.
        receiver.bindReceiverEventListener(mInternalReceiverEventListener);
        receiver.bindStateGetter(mStateGetter);
        if (receiver instanceof BaseCover) {
            BaseCover cover = (BaseCover) receiver;
            //add cover view to cover strategy container.
            mCoverStrategy.addCover(cover);
            PLog.d(TAG, "on cover attach : " + cover.getTag() + " ," + cover.getCoverLevel());
        }
    }

    //detach receiver, unbind receiver event listener
    // and remove cover container if it is a cover instance.
    private void detachReceiver(IReceiver receiver) {
        if (receiver instanceof BaseCover) {
            BaseCover cover = (BaseCover) receiver;
            //remove cover view to cover strategy container.
            mCoverStrategy.removeCover(cover);
            PLog.w(TAG, "on cover detach : " + cover.getTag() + " ," + cover.getCoverLevel());
        }
        //unbind the ReceiverEventListener for receivers connect.
        receiver.bindReceiverEventListener(null);
        receiver.bindStateGetter(null);
    }

    public void destroy() {
        //clear ReceiverGroupChangeListener
        if (mReceiverGroup != null) {
            mReceiverGroup.removeOnReceiverGroupChangeListener(mInternalReceiverGroupChangeListener);
        }
        //destroy producer group
        mProducerGroup.destroy();
        //and remove render view.
        removeRender();
        //and remove all covers
        removeAllCovers();
    }

    private void removeRender() {
        if (mRenderContainer != null)
            mRenderContainer.removeAllViews();
    }

    protected void removeAllCovers() {
        mCoverStrategy.removeAllCovers();
        PLog.d(TAG, "detach all covers");
    }

    //----------------------------------dispatch gesture touch event---------------------------------

    @Override
    public void onSingleTapUp(MotionEvent event) {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchTouchEventOnSingleTabUp(event);
    }

    @Override
    public void onDoubleTap(MotionEvent event) {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchTouchEventOnDoubleTabUp(event);
    }

    @Override
    public void onDown(MotionEvent event) {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchTouchEventOnDown(event);
    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchTouchEventOnScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onEndGesture() {
        if (mEventDispatcher != null)
            mEventDispatcher.dispatchTouchEventOnEndGesture();
    }
}
