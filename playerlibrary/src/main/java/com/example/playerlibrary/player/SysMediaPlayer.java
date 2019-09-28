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

package com.example.playerlibrary.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.example.playerlibrary.config.AppContextAttach;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.BundlePool;
import com.example.playerlibrary.event.EventKey;
import com.example.playerlibrary.event.OnErrorEventListener;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.log.PLog;

import java.io.FileDescriptor;
import java.util.HashMap;

/**
 * Created by Taurus on 2018/3/17.
 */

public class SysMediaPlayer extends BaseInternalPlayer {

    final String TAG = "SysMediaPlayer";

    private final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;

    private MediaPlayer mMediaPlayer;

    private int mTargetState;

    private long mBandWidth;
    private int mVideoWidth;
    private int mVideoHeight;
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    Bundle bundle = BundlePool.obtain();
                    bundle.putInt(EventKey.INT_ARG1, mVideoWidth);
                    bundle.putInt(EventKey.INT_ARG2, mVideoHeight);
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE, bundle);
                }
            };
    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    updateStatus(STATE_PLAYBACK_COMPLETE);
                    mTargetState = STATE_PLAYBACK_COMPLETE;
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE, null);
                }
            };
    private int startSeekPos;
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            PLog.d(TAG, "onPrepared...");
            updateStatus(STATE_PREPARED);

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_ARG1, mVideoWidth);
            bundle.putInt(EventKey.INT_ARG2, mVideoHeight);

            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED, bundle);

            int seekToPosition = startSeekPos;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                //seek to start position
                mMediaPlayer.seekTo(seekToPosition);
                startSeekPos = 0;
            }

            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            PLog.d(TAG, "mTargetState = " + mTargetState);
            if (mTargetState == STATE_STARTED) {
                start();
            } else if (mTargetState == STATE_PAUSED) {
                pause();
            } else if (mTargetState == STATE_STOPPED
                    || mTargetState == STATE_IDLE) {
                reset();
            }
        }
    };
    private MediaPlayer.OnInfoListener mInfoListener =
            new MediaPlayer.OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int arg1, int arg2) {
                    switch (arg1) {
                        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            PLog.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            PLog.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                            startSeekPos = 0;
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START, null);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            PLog.d(TAG, "MEDIA_INFO_BUFFERING_START:" + arg2);
                            Bundle bundle = BundlePool.obtain();
                            bundle.putLong(EventKey.LONG_DATA, mBandWidth);
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START, bundle);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            PLog.d(TAG, "MEDIA_INFO_BUFFERING_END:" + arg2);
                            Bundle bundle1 = BundlePool.obtain();
                            bundle1.putLong(EventKey.LONG_DATA, mBandWidth);
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END, bundle1);
                            break;
                        case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            PLog.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BAD_INTERLEAVING, null);
                            break;
                        case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            PLog.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_NOT_SEEK_ABLE, null);
                            break;
                        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            PLog.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_METADATA_UPDATE, null);
                            break;
                        case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            PLog.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_UNSUPPORTED_SUBTITLE, null);
                            break;
                        case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            PLog.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SUBTITLE_TIMED_OUT, null);
                            break;
                        case MEDIA_INFO_NETWORK_BANDWIDTH:
                            PLog.d(TAG, "band_width : " + arg2);
                            mBandWidth = arg2 * 1000;
                            break;
                    }
                    return true;
                }
            };
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            PLog.d(TAG, "EVENT_CODE_SEEK_COMPLETE");
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE, null);
        }
    };
    private MediaPlayer.OnErrorListener mErrorListener =
            new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                    PLog.d(TAG, "Error: " + framework_err + "," + impl_err);
                    updateStatus(STATE_ERROR);
                    mTargetState = STATE_ERROR;

                    int eventCode = OnErrorEventListener.ERROR_EVENT_COMMON;

                    switch (framework_err) {
                        case MediaPlayer.MEDIA_ERROR_IO:
                            eventCode = OnErrorEventListener.ERROR_EVENT_IO;
                            break;
                        case MediaPlayer.MEDIA_ERROR_MALFORMED:
                            eventCode = OnErrorEventListener.ERROR_EVENT_MALFORMED;
                            break;
                        case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                            eventCode = OnErrorEventListener.ERROR_EVENT_TIMED_OUT;
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            eventCode = OnErrorEventListener.ERROR_EVENT_UNKNOWN;
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                            eventCode = OnErrorEventListener.ERROR_EVENT_UNSUPPORTED;
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            eventCode = OnErrorEventListener.ERROR_EVENT_SERVER_DIED;
                            break;
                        case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                            eventCode = OnErrorEventListener.ERROR_EVENT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
                            break;
                    }

                    /* If an error handler has been supplied, use it and finish. */
                    Bundle bundle = BundlePool.obtain();
                    submitErrorEvent(eventCode, bundle);
                    return true;
                }
            };
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    submitBufferingUpdate(percent, null);
                }
            };

    public SysMediaPlayer() {
        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                stop();
                reset();
                resetListener();
            }
            // REMOVED: mAudioSession
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            updateStatus(STATE_INITIALIZED);

            String data = dataSource.getData();
            Uri uri = dataSource.getUri();
            HashMap<String, String> headers = dataSource.getExtra();
            FileDescriptor fileDescriptor = dataSource.getFileDescriptor();
            AssetFileDescriptor assetFileDescriptor = dataSource.getAssetFileDescriptor();
            if (data != null) {
                mMediaPlayer.setDataSource(data);
            } else if (uri != null) {
                Context applicationContext = AppContextAttach.getApplicationContext();
                if (headers == null)
                    mMediaPlayer.setDataSource(applicationContext, uri);
                else
                    mMediaPlayer.setDataSource(applicationContext, uri, headers);
            } else if (fileDescriptor != null) {
                mMediaPlayer.setDataSource(fileDescriptor);
            } else if (assetFileDescriptor != null
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mMediaPlayer.setDataSource(assetFileDescriptor);
            }

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            Bundle bundle = BundlePool.obtain();
            bundle.putSerializable(EventKey.SERIALIZABLE_DATA, dataSource);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(STATE_ERROR);
            mTargetState = STATE_ERROR;
        }
    }

    private boolean available() {
        return mMediaPlayer != null;
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        try {
            if (available()) {
                mMediaPlayer.setDisplay(surfaceHolder);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SURFACE_HOLDER_UPDATE, null);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            if (available()) {
                mMediaPlayer.setSurface(surface);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SURFACE_UPDATE, null);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void setVolume(float left, float right) {
        if (available()) {
            mMediaPlayer.setVolume(left, right);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (available() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
            playbackParams.setSpeed(speed);
            mMediaPlayer.setPlaybackParams(playbackParams);
        } else {
            PLog.e(TAG, "not support play speed setting.");
        }
    }

    @Override
    public boolean isPlaying() {
        if (available() && getState() != STATE_ERROR) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        if (available() && (getState() == STATE_PREPARED
                || getState() == STATE_STARTED
                || getState() == STATE_PAUSED
                || getState() == STATE_PLAYBACK_COMPLETE)) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (available()
                && getState() != STATE_ERROR
                && getState() != STATE_INITIALIZED
                && getState() != STATE_IDLE) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getAudioSessionId() {
        if (available()) {
            return mMediaPlayer.getAudioSessionId();
        }
        return 0;
    }

    @Override
    public int getVideoWidth() {
        if (available()) {
            return mMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (available()) {
            return mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public void start() {
        try {
            if (available() &&
                    (getState() == STATE_PREPARED
                            || getState() == STATE_PAUSED
                            || getState() == STATE_PLAYBACK_COMPLETE)) {
                mMediaPlayer.start();
                updateStatus(STATE_STARTED);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_START, null);
            }
        } catch (Exception e) {
            handleException(e);
        }
        mTargetState = STATE_STARTED;
    }

    @Override
    public void start(int msc) {
        if (available()) {
            if (msc > 0) {
                startSeekPos = msc;
            }
            start();
        }
    }

    @Override
    public void pause() {
        try {
            if (available()) {
                mMediaPlayer.pause();
                updateStatus(STATE_PAUSED);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE, null);
            }
        } catch (Exception e) {
            handleException(e);
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public void resume() {
        try {
            if (available() && getState() == STATE_PAUSED) {
                mMediaPlayer.start();
                updateStatus(STATE_STARTED);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_RESUME, null);
            }
        } catch (Exception e) {
            handleException(e);
        }
        mTargetState = STATE_STARTED;
    }

    @Override
    public void seekTo(int msc) {
        if (available() &&
                (getState() == STATE_PREPARED
                        || getState() == STATE_STARTED
                        || getState() == STATE_PAUSED
                        || getState() == STATE_PLAYBACK_COMPLETE)) {
            mMediaPlayer.seekTo(msc);
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_DATA, msc);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO, bundle);
        }
    }

    @Override
    public void stop() {
        if (available() &&
                (getState() == STATE_PREPARED
                        || getState() == STATE_STARTED
                        || getState() == STATE_PAUSED
                        || getState() == STATE_PLAYBACK_COMPLETE)) {
            mMediaPlayer.stop();
            updateStatus(STATE_STOPPED);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STOP, null);
        }
        mTargetState = STATE_STOPPED;
    }

    @Override
    public void reset() {
        if (available()) {
            mMediaPlayer.reset();
            updateStatus(STATE_IDLE);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_RESET, null);
        }
        mTargetState = STATE_IDLE;
    }

    @Override
    public void destroy() {
        if (available()) {
            updateStatus(STATE_END);
            resetListener();
            mMediaPlayer.release();
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_DESTROY, null);
        }
    }

    private void handleException(Exception e) {
        if (e != null)
            e.printStackTrace();
        reset();
    }

    private void resetListener() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnVideoSizeChangedListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.setOnBufferingUpdateListener(null);
    }
}
