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

package com.example.playerlibrary.assist;

import android.view.ViewGroup;

import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.OnErrorEventListener;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.provider.IDataProvider;
import com.example.playerlibrary.receiver.IReceiverGroup;
import com.example.playerlibrary.receiver.OnReceiverEventListener;


/**
 * Created by Taurus on 2018/5/21.
 * <p>
 * The Association for auxiliary view containers and players
 */
public interface AssistPlay {

    void setOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener);

    void setOnErrorEventListener(OnErrorEventListener onErrorEventListener);

    void setOnReceiverEventListener(OnReceiverEventListener onReceiverEventListener);

    void setOnProviderListener(IDataProvider.OnProviderListener onProviderListener);

    void setDataProvider(IDataProvider dataProvider);

    boolean switchDecoder(int decoderPlanId);

    void setVolume(float left, float right);

    void setSpeed(float speed);

    void setReceiverGroup(IReceiverGroup receiverGroup);

    void attachContainer(ViewGroup userContainer);

    void setDataSource(DataSource dataSource);

    void play();

    void play(boolean updateRender);

    boolean isInPlaybackState();

    boolean isPlaying();

    int getCurrentPosition();

    int getDuration();

    int getAudioSessionId();

    int getBufferPercentage();

    int getState();

    void rePlay(int msc);

    void pause();

    void resume();

    void seekTo(int msc);

    void stop();

    void reset();

    void destroy();

}
