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

package com.example.playerlibrary.extension;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.playerlibrary.assist.InterKey;
import com.example.playerlibrary.log.PLog;
import com.example.playerlibrary.utils.NetworkUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Taurus on 2018/5/27.
 * <p>
 * Network change event producer, used to send network status change events.
 */
public class NetworkEventProducer extends BaseEventProducer {

    private static final int MSG_CODE_NETWORK_CHANGE = 100;
    private final String TAG = "NetworkEventProducer";
    private Context mAppContext;

    private NetChangeBroadcastReceiver mBroadcastReceiver;

    private int mState;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CODE_NETWORK_CHANGE:
                    int state = (int) msg.obj;
                    if (mState == state)
                        return;
                    mState = state;
                    ReceiverEventSender sender = getSender();
                    if (sender != null) {
                        sender.sendInt(InterKey.KEY_NETWORK_STATE, mState);
                        PLog.d(TAG, "onNetworkChange : " + mState);
                    }
                    break;
            }
        }
    };

    public NetworkEventProducer(Context context) {
        this.mAppContext = context.getApplicationContext();
    }

    private void registerNetChangeReceiver() {
        unregisterNetChangeReceiver();
        if (mAppContext != null) {
            mBroadcastReceiver = new NetChangeBroadcastReceiver(mAppContext, mHandler);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mAppContext.registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    private void unregisterNetChangeReceiver() {
        try {
            if (mAppContext != null && mBroadcastReceiver != null) {
                mAppContext.unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdded() {
        mState = NetworkUtils.getNetworkState(mAppContext);
        registerNetChangeReceiver();
    }

    @Override
    public void onRemoved() {
        destroy();
    }

    public void destroy() {
        if (mBroadcastReceiver != null)
            mBroadcastReceiver.destroy();
        unregisterNetChangeReceiver();
        mHandler.removeMessages(MSG_CODE_NETWORK_CHANGE);
    }

    public static class NetChangeBroadcastReceiver extends BroadcastReceiver {

        private Handler handler;
        private WeakReference<Context> mContextRefer;
        private Runnable mDelayRunnable = new Runnable() {
            @Override
            public void run() {
                if (mContextRefer != null && mContextRefer.get() != null) {
                    int networkState = NetworkUtils.getNetworkState(mContextRefer.get());
                    Message message = Message.obtain();
                    message.what = MSG_CODE_NETWORK_CHANGE;
                    message.obj = networkState;
                    handler.sendMessage(message);
                }
            }
        };

        public NetChangeBroadcastReceiver(Context context, Handler handler) {
            mContextRefer = new WeakReference<>(context);
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                handler.removeCallbacks(mDelayRunnable);
                handler.postDelayed(mDelayRunnable, 1000);
            }
        }

        public void destroy() {
            handler.removeCallbacks(mDelayRunnable);
        }
    }

}
