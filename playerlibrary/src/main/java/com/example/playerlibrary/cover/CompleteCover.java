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

package com.example.playerlibrary.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.playerlibrary.R;
import com.example.playerlibrary.assist.DataInter;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.receiver.BaseCover;
import com.example.playerlibrary.receiver.IReceiverGroup;


/**
 * Created by Taurus on 2018/4/20.
 */

public class CompleteCover extends BaseCover {

    private TextView mReplay;
    private TextView mNext;
    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[]{DataInter.Key.KEY_IS_HAS_NEXT};
                }

                @Override
                public void onValueUpdate(String key, Object value) {
                    if (key.equals(DataInter.Key.KEY_IS_HAS_NEXT)) {
                        setNextState((Boolean) value);
                    }
                }
            };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.tv_replay) {
                requestReplay(null);

            } else if (i == R.id.tv_next) {
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_NEXT, null);

            }
            setPlayCompleteState(false);
        }
    };

    public CompleteCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mReplay = findViewById(R.id.tv_replay);
        mNext = findViewById(R.id.tv_next);

        mReplay.setOnClickListener(mOnClickListener);
        mNext.setOnClickListener(mOnClickListener);

        setNextState(false);

        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        if (getGroupValue().getBoolean(DataInter.Key.KEY_COMPLETE_SHOW)) {
            setPlayCompleteState(true);
        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        setCoverVisibility(View.GONE);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    private void setNextState(boolean state) {
        mNext.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setPlayCompleteState(boolean state) {
        setCoverVisibility(state ? View.VISIBLE : View.GONE);
        getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, state);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                setPlayCompleteState(false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                setPlayCompleteState(true);
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_complete_cover, null);
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(20);
    }
}
