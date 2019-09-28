package com.example.playerlibrary.assist;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.playerlibrary.config.AppContextAttach;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.OnErrorEventListener;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.log.PLog;
import com.example.playerlibrary.player.IPlayer;
import com.example.playerlibrary.provider.IDataProvider;
import com.example.playerlibrary.receiver.IReceiverGroup;
import com.example.playerlibrary.receiver.OnReceiverEventListener;
import com.example.playerlibrary.receiver.ReceiverGroup;

import java.util.ArrayList;
import java.util.List;

public class AssistPlayer {

    private static AssistPlayer i;
    private RelationAssist mRelationAssist;
    private Context mAppContext;

    private DataSource mDataSource;
    private List<OnPlayerEventListener> mOnPlayerEventListeners;
    private List<OnErrorEventListener> mOnErrorEventListeners;
    private List<OnReceiverEventListener> mOnReceiverEventListeners;
    private OnPlayerEventListener mInternalPlayerEventListener =
            new OnPlayerEventListener() {
                @Override
                public void onPlayerEvent(int eventCode, Bundle bundle) {
                    callBackPlayerEventListeners(eventCode, bundle);
                }
            };
    private OnErrorEventListener mInternalErrorEventListener =
            new OnErrorEventListener() {
                @Override
                public void onErrorEvent(int eventCode, Bundle bundle) {
                    callBackErrorEventListeners(eventCode, bundle);
                }
            };
    private OnReceiverEventListener mInternalReceiverEventListener =
            new OnReceiverEventListener() {
                @Override
                public void onReceiverEvent(int eventCode, Bundle bundle) {
                    callBackReceiverEventListeners(eventCode, bundle);
                }
            };
    private OnAssistPlayEventHandler mInternalEventAssistHandler =
            new OnAssistPlayEventHandler() {
                @Override
                public void onAssistHandle(AssistPlay assistPlay, int eventCode, Bundle bundle) {
                    super.onAssistHandle(assistPlay, eventCode, bundle);
                    switch (eventCode) {
                        case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                            reset();
                            break;
                    }
                }
            };

    private AssistPlayer() {
        mAppContext = AppContextAttach.getApplicationContext();
        mRelationAssist = new RelationAssist(mAppContext);
        mRelationAssist.setEventAssistHandler(mInternalEventAssistHandler);
        mRelationAssist.getSuperContainer().setBackgroundColor(Color.BLACK);
        mOnPlayerEventListeners = new ArrayList<>();
        mOnErrorEventListeners = new ArrayList<>();
        mOnReceiverEventListeners = new ArrayList<>();
    }

    public static AssistPlayer get() {
        if (null == i) {
            synchronized (AssistPlayer.class) {
                if (null == i) {
                    i = new AssistPlayer();
                }
            }
        }
        return i;
    }

    public void addOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        if (mOnPlayerEventListeners.contains(onPlayerEventListener))
            return;
        mOnPlayerEventListeners.add(onPlayerEventListener);
    }

    public boolean removePlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        return mOnPlayerEventListeners.remove(onPlayerEventListener);
    }

    public void addOnErrorEventListener(OnErrorEventListener onErrorEventListener) {
        if (mOnErrorEventListeners.contains(onErrorEventListener))
            return;
        mOnErrorEventListeners.add(onErrorEventListener);
    }

    public boolean removeErrorEventListener(OnErrorEventListener onErrorEventListener) {
        return mOnErrorEventListeners.remove(onErrorEventListener);
    }

    public void addOnReceiverEventListener(OnReceiverEventListener onReceiverEventListener) {
        if (mOnReceiverEventListeners.contains(onReceiverEventListener))
            return;
        mOnReceiverEventListeners.add(onReceiverEventListener);
    }

    public boolean removeReceiverEventListener(OnReceiverEventListener onReceiverEventListener) {
        return mOnReceiverEventListeners.remove(onReceiverEventListener);
    }

    private void callBackPlayerEventListeners(int eventCode, Bundle bundle) {
        for (OnPlayerEventListener listener : mOnPlayerEventListeners) {
            listener.onPlayerEvent(eventCode, bundle);
        }
    }

    private void callBackErrorEventListeners(int eventCode, Bundle bundle) {
        for (OnErrorEventListener listener : mOnErrorEventListeners) {
            listener.onErrorEvent(eventCode, bundle);
        }
    }

    private void callBackReceiverEventListeners(int eventCode, Bundle bundle) {
        for (OnReceiverEventListener listener : mOnReceiverEventListeners) {
            listener.onReceiverEvent(eventCode, bundle);
        }
    }

    private void attachListener() {
        mRelationAssist.setOnPlayerEventListener(mInternalPlayerEventListener);
        mRelationAssist.setOnErrorEventListener(mInternalErrorEventListener);
        mRelationAssist.setOnReceiverEventListener(mInternalReceiverEventListener);
    }

    public IReceiverGroup getReceiverGroup() {
        return mRelationAssist.getReceiverGroup();
    }

    public void setReceiverGroup(ReceiverGroup receiverGroup) {
        mRelationAssist.setReceiverGroup(receiverGroup);
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public void play(ViewGroup userContainer, DataSource dataSource) {
        if (dataSource != null) {
            this.mDataSource = dataSource;
        }
        attachListener();
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup != null && dataSource != null) {
            receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, false);
        }
        mRelationAssist.attachContainer(userContainer, dataSource == null);
        if (dataSource != null)
            mRelationAssist.setDataSource(dataSource);
        if (receiverGroup != null
                && receiverGroup.getGroupValue().getBoolean(DataInter.Key.KEY_ERROR_SHOW)) {
            return;
        }
        if (dataSource != null)
            mRelationAssist.play(true);
    }

    public void setDataProvider(IDataProvider dataProvider) {
        mRelationAssist.setDataProvider(dataProvider);
    }

    public boolean isInPlaybackState() {
        int state = getState();
        PLog.d("AssistPlayer", "isInPlaybackState : state = " + state);
        return state != IPlayer.STATE_END
                && state != IPlayer.STATE_ERROR
                && state != IPlayer.STATE_IDLE
                && state != IPlayer.STATE_INITIALIZED
                && state != IPlayer.STATE_PLAYBACK_COMPLETE
                && state != IPlayer.STATE_STOPPED;
    }

    public boolean isPlaying() {
        return mRelationAssist.isPlaying();
    }

    public int getState() {
        return mRelationAssist.getState();
    }

    public void pause() {
        mRelationAssist.pause();
    }

    public void resume() {
        mRelationAssist.resume();
    }

    public void stop() {
        mRelationAssist.stop();
    }

    public void reset() {
        mRelationAssist.reset();
    }

    public void destroy() {
        mOnPlayerEventListeners.clear();
        mOnErrorEventListeners.clear();
        mOnReceiverEventListeners.clear();
        mRelationAssist.destroy();
        i = null;
    }
}
