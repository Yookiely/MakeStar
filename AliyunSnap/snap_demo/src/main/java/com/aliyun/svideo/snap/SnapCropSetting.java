/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.snap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.apsaravideo.recorder.AliyunVideoRecorder;
import com.aliyun.common.utils.StorageUtils;
import com.aliyun.snap.crop.AliyunVideoCropActivity;
import com.aliyun.snap.crop.MediaActivity;
import com.aliyun.snap.R;
import com.aliyun.svideo.sdk.external.struct.common.VideoDisplayMode;
import com.aliyun.svideo.sdk.external.struct.common.VideoQuality;
import com.aliyun.svideo.sdk.external.struct.encoder.VideoCodecs;
import com.aliyun.svideo.sdk.external.struct.snap.AliyunSnapVideoParam;
import com.aliyun.svideo.sdk.external.struct.common.CropKey;
import com.aliyun.svideo.common.utils.FastClickUtil;
import com.aliyun.svideo.common.utils.PermissionUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/1/13.
 */

public class SnapCropSetting extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    String[] effectDirs;

    private static final int PROGRESS_1_1 = 33;
    private static final int PROGRESS_3_4 = 66;
    private static final int PROGRESS_9_16 = 100;

    private static final int PROGRESS_360P = 25;
    private static final int PROGRESS_480P = 50;
    private static final int PROGRESS_540P = 75;
    private static final int PROGRESS_720P = 100;

    private static final int PROGRESS_LOW = 25;
    private static final int PROGRESS_MEIDAN = 50;
    private static final int PROGRESS_HIGH = 75;
    private static final int PROGRESS_SUPER = 100;

    private static final int DEFAULT_FRAMR_RATE = 25;
    private static final int DEFAULT_GOP = 5;
    private static final int REQUEST_CROP = 2002;

    private TextView startImport;
    private SeekBar resolutionSeekbar, qualitySeekbar, ratioSeekbar;
    private TextView ratioTxt, qualityTxt, resolutionTxt, uploadTxt;
    private RadioButton radioFill, radioCrop;
    //视频编码方式选择按钮
    private RadioButton mEncorderHardwareBtn, mEncorderOpenh264Btn, mEncorderFfmpegBtn;
    private EditText frameRateEdit, gopEdit;
    private ImageView back;
    private int resolutionMode, ratioMode;
    private VideoQuality videoQulity;
    private VideoDisplayMode cropMode = VideoDisplayMode.FILL;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;
    private AsyncTask<Void, Void, Void> mCopyAssetsTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aliyun_svideo_activity_snap_crop_setting);
        initView();
        initAssetPath();
        copyAssets();
    }

    private void copyAssets() {

        mCopyAssetsTask = new CopyAssetsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class CopyAssetsTask extends AsyncTask<Void, Void, Void> {
        WeakReference<SnapCropSetting> weakReference;

        public CopyAssetsTask(SnapCropSetting activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (weakReference == null) {
                return null;
            }
            SnapCropSetting snapCropSetting = weakReference.get();
            SnapCommon.copyAll(snapCropSetting);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (weakReference == null) {
                return;
            }
            SnapCropSetting snapCropSetting = weakReference.get();
            if (snapCropSetting != null) {
                snapCropSetting.startImport.setEnabled(true);
                snapCropSetting.initAssetPath();
            }
        }
    }

    private void initAssetPath() {
        String path = StorageUtils.getCacheDirectory(this).getAbsolutePath() + File.separator + SnapCommon.QU_NAME + File.separator;
        File filter = new File(new File(path), "filter");

        String[] list = filter.list();
        if (list == null || list.length == 0) {
            return ;
        }
        effectDirs = new String[list.length + 1];
        effectDirs[0] = null;
        for (int i = 0; i < list.length; i++) {
            effectDirs[i + 1] = filter.getPath() + "/" + list[i];
        }
//        effectDirs = new String[]{
//                null,
//                path + "filter/chihuang",
//                path + "filter/fentao",
//                path + "filter/hailan",
//                path + "filter/hongrun",
//                path + "filter/huibai",
//                path + "filter/jingdian",
//                path + "filter/maicha",
//                path + "filter/nonglie",
//                path + "filter/rourou",
//                path + "filter/shanyao",
//                path + "filter/xianguo",
//                path + "filter/xueli",
//                path + "filter/yangguang",
//                path + "filter/youya",
//                path + "filter/zhaoyang"
//        };
    }


    private void initView() {
        startImport = (TextView) findViewById(R.id.start_import);
        startImport.setOnClickListener(this);
        startImport.setEnabled(false);
        resolutionSeekbar = (SeekBar) findViewById(R.id.resolution_seekbar);
        qualitySeekbar = (SeekBar) findViewById(R.id.quality_seekbar);
        ratioSeekbar = (SeekBar) findViewById(R.id.ratio_seekbar);
        ratioTxt = (TextView) findViewById(R.id.ratio_txt);
        qualityTxt = (TextView) findViewById(R.id.quality_txt);
        resolutionTxt = (TextView) findViewById(R.id.resolution_txt);
        uploadTxt = (TextView) findViewById(R.id.upload_progress);
        uploadTxt.setVisibility(View.GONE);
        radioCrop = (RadioButton) findViewById(R.id.radio_crop);
        radioCrop.setOnCheckedChangeListener(this);
        radioFill = (RadioButton) findViewById(R.id.radio_fill);
        radioFill.setOnCheckedChangeListener(this);
        back = (ImageView) findViewById(R.id.aliyun_back);
        back.setOnClickListener(this);
        frameRateEdit = (EditText) findViewById(R.id.frame_rate_edit);
        gopEdit = (EditText) findViewById(R.id.gop_edit);
        ratioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= PROGRESS_1_1) {
                    ratioMode = AliyunSnapVideoParam.RATIO_MODE_1_1;
                    ratioTxt.setText(R.string.alivc_snap_crop_ratio_1_1);
                } else if (progress > PROGRESS_1_1 && progress <= PROGRESS_3_4) {
                    ratioMode = AliyunSnapVideoParam.RATIO_MODE_3_4;
                    ratioTxt.setText(R.string.alivc_snap_crop_ratio_3_4);
                } else if (progress > PROGRESS_3_4) {
                    ratioMode = AliyunSnapVideoParam.RATIO_MODE_9_16;
                    ratioTxt.setText(R.string.alivc_snap_crop_ratio_9_16);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= PROGRESS_1_1) {
                    seekBar.setProgress(PROGRESS_1_1);
                } else if (progress > PROGRESS_1_1 && progress <= PROGRESS_3_4) {
                    seekBar.setProgress(PROGRESS_3_4);
                } else if (progress > PROGRESS_3_4) {
                    seekBar.setProgress(PROGRESS_9_16);
                }
            }
        });
        qualitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= PROGRESS_LOW) {
                    videoQulity = VideoQuality.LD;
                    qualityTxt.setText(R.string.aliyun_svideo_crop_quality_low);
                } else if (progress > PROGRESS_LOW && progress <= PROGRESS_MEIDAN) {
                    videoQulity = VideoQuality.SD;
                    qualityTxt.setText(R.string.alivc_snap_quality_median);
                } else if (progress > PROGRESS_MEIDAN && progress <= PROGRESS_HIGH) {
                    videoQulity = VideoQuality.HD;
                    qualityTxt.setText(R.string.alivc_snap_quality_high);
                } else if (progress > PROGRESS_HIGH) {
                    videoQulity = VideoQuality.SSD;
                    qualityTxt.setText(R.string.alivc_snap_quality_super);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= PROGRESS_LOW) {
                    seekBar.setProgress(PROGRESS_LOW);
                } else if (progress > PROGRESS_LOW && progress <= PROGRESS_MEIDAN) {
                    seekBar.setProgress(PROGRESS_MEIDAN);
                } else if (progress > PROGRESS_MEIDAN && progress <= PROGRESS_HIGH) {
                    seekBar.setProgress(PROGRESS_HIGH);
                } else if (progress > PROGRESS_HIGH) {
                    seekBar.setProgress(PROGRESS_SUPER);
                }
            }
        });
        resolutionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= PROGRESS_360P) {
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_360P;
                    resolutionTxt.setText(R.string.alivc_snap_resolution_360p);
                } else if (progress > PROGRESS_360P && progress <= PROGRESS_480P) {
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_480P;
                    resolutionTxt.setText(R.string.alivc_snap_resolution_480p);
                } else if (progress > PROGRESS_480P && progress <= PROGRESS_540P) {
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_540P;
                    resolutionTxt.setText(R.string.alivc_snap_resolution_540p);
                } else if (progress > PROGRESS_540P) {
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_720P;
                    resolutionTxt.setText(R.string.alivc_snap_resolution_720p);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress < PROGRESS_360P) {
                    seekBar.setProgress(PROGRESS_360P);
                } else if (progress > PROGRESS_360P && progress <= PROGRESS_480P) {
                    seekBar.setProgress(PROGRESS_480P);
                } else if (progress > PROGRESS_480P && progress <= PROGRESS_540P) {
                    seekBar.setProgress(PROGRESS_540P);
                } else if (progress > PROGRESS_540P) {
                    seekBar.setProgress(PROGRESS_720P);
                }
            }
        });
        ratioSeekbar.setProgress(PROGRESS_3_4);
        resolutionSeekbar.setProgress(PROGRESS_540P);
        qualitySeekbar.setProgress(PROGRESS_HIGH);

        //视频编码相关按钮
        mEncorderHardwareBtn = findViewById(R.id.alivc_crop_encoder_hardware);
        mEncorderOpenh264Btn = findViewById(R.id.alivc_crop_encoder_openh264);
        mEncorderFfmpegBtn = findViewById(R.id.alivc_crop_encoder_ffmpeg);
        mEncorderHardwareBtn.setOnCheckedChangeListener(this);
        mEncorderOpenh264Btn.setOnCheckedChangeListener(this);
        mEncorderFfmpegBtn.setOnCheckedChangeListener(this);

        onEncoderSelected(mEncorderHardwareBtn);
    }
    /**
     * 视频编码方式选择
     *
     * @param view
     */
    private void onEncoderSelected(View view) {

        if (view == mEncorderFfmpegBtn) {
            mVideoCodec = VideoCodecs.H264_SOFT_FFMPEG;
            mEncorderFfmpegBtn.setChecked(true);
            mEncorderHardwareBtn.setChecked(false);
            mEncorderOpenh264Btn.setChecked(false);
        } else if (view == mEncorderOpenh264Btn) {
            mEncorderOpenh264Btn.setChecked(true);
            mEncorderFfmpegBtn.setChecked(false);
            mEncorderHardwareBtn.setChecked(false);
            mVideoCodec = VideoCodecs.H264_SOFT_OPENH264;
        } else {
            mEncorderHardwareBtn.setChecked(true);
            mEncorderFfmpegBtn.setChecked(false);
            mEncorderOpenh264Btn.setChecked(false);
            mVideoCodec = VideoCodecs.H264_HARDWARE;
        }
    }

    /**
     * 权限申请
     */
    String[] permission = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;

        // 判断是否所有的权限都已经授予了
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }

        if (!isAllGranted) {
            // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
            Toast.makeText(this, com.aliyun.apsaravideo.R.string.alivc_recorder_camera_permission_tip, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == startImport) {
            if (!PermissionUtils.checkPermissionsGroup(this, permission)) {
                PermissionUtils.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
                return;
            }

            String inputGop = gopEdit.getText().toString();
            int gop = DEFAULT_GOP;
            if (!TextUtils.isEmpty(inputGop)) {
                try {
                    gop = Integer.parseInt(inputGop);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }


            String inputFrame = frameRateEdit.getText().toString();
            int frameRate = DEFAULT_FRAMR_RATE;

            if (!TextUtils.isEmpty(inputFrame)) {
                try {
                    frameRate = Integer.parseInt(inputFrame);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            AliyunSnapVideoParam mCropParam = new AliyunSnapVideoParam.Builder()
            .setFrameRate(frameRate)//设置帧率
            .setGop(gop)//设置关键帧间隔
            .setCropMode(cropMode)//裁剪模式
            .setVideoQuality(videoQulity)//视频质量
            .setResolutionMode(resolutionMode)//分辨率
            .setRatioMode(ratioMode)//视频比例
            .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//显示分类SORT_MODE_VIDEO视频;SORT_MODE_PHOTO图片;SORT_MODE_MERGE图片和视频
            .setNeedRecord(false)//资源显示控件是否需要能跳录制
            .setCropUseGPU(false)//GPU默认关闭
            .setVideoCodec(mVideoCodec)
            .build();
            if (!FastClickUtil.isFastClickActivity(AliyunVideoCropActivity.class.getSimpleName())) {

                AliyunVideoCropActivity.startCropForResult(this, REQUEST_CROP, mCropParam);
            }
        } else if (v ==  back) {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int type = data.getIntExtra(MediaActivity.RESULT_TYPE, 0);
                if (type ==  MediaActivity.RESULT_TYPE_CROP) {
                    String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                    Toast.makeText(this, getResources().getString(R.string.alivc_snap_record_file_path) + path + getResources().getString(R.string.alivc_snap_record_duration) + data.getLongExtra(CropKey.RESULT_KEY_DURATION, 0), Toast.LENGTH_SHORT).show();
                } else if (type ==  MediaActivity.RESULT_TYPE_RECORD) {
                    //录制的文件
                    Toast.makeText(this, getResources().getString(R.string.alivc_snap_record_file_path) + data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.alivc_snap_crop_cancel), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == radioCrop) {
                cropMode = AliyunVideoCropActivity.SCALE_CROP;
                if (radioFill != null) {
                    radioFill.setChecked(false);
                }
            } else if (buttonView ==  radioFill) {
                cropMode = AliyunVideoCropActivity.SCALE_FILL;
                if (radioCrop != null) {
                    radioCrop.setChecked(false);
                }
            } else if (buttonView == mEncorderFfmpegBtn || buttonView == mEncorderOpenh264Btn || buttonView == mEncorderHardwareBtn ) {
                onEncoderSelected(buttonView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCopyAssetsTask != null) {
            mCopyAssetsTask.cancel(true);
            mCopyAssetsTask = null;
        }
    }
}
