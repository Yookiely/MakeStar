//package com.alibaba.sdk.android.vodupload_demo.old;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
//import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
//import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
//import com.alibaba.sdk.android.vod.upload.common.utils.StringUtil;
//import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
//import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
//import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
//import com.alibaba.sdk.android.vodupload_demo.FileUtils;
//import com.alibaba.sdk.android.vodupload_demo.R;
//
//import java.io.File;
//
///**
// * 短视频上传场景示例
// * Created by Mulberry on 2017/11/24.
// * sts上传方式已不再维护，推荐使用点播凭证方式上传
// */
//@Deprecated
//public class SVideoUploadActivity extends AppCompatActivity {
//
//    private static final String TAG = "VOD_UPLOAD";
//    Button btnUpload;
//    Button btnCancel;
//    Button btnResume;
//    Button btnPause;
//    TextView tvProgress;
//    private long  progress;
//
//    private String videoPath;//视频路径
//    private String imagePath;//封面图片路径
//
//    //以下四个值由开发者的服务端提供,参考文档：https://help.aliyun.com/document_detail/28756.html（STS介绍）
//    // AppServer STS SDK参考：https://help.aliyun.com/document_detail/28788.html
//    private String accessKeyId;//子accessKeyId
//    private String accessKeySecret;//子accessKeySecret
//    private String securityToken;
//    private String expriedTime;//STS的过期时间
//
//    private String requestID = null;//传空或传递访问STS返回的requestID
//
//    VODSVideoUploadClient vodsVideoUploadClient;
//    public static String VOD_REGION = "cn-shanghai";
//    public static boolean VOD_RECORD_UPLOAD_PROGRESS_ENABLED = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.svideo_upload);
//        getIntentExtra();
//
//        getFileTask.execute();
//
//        btnUpload = (Button)findViewById(R.id.btn_upload);
//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isCreateFile) {
//                    Toast.makeText(v.getContext(), "Please Wait, The videoPath and imagePath is null", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (StringUtil.isEmpty(accessKeyId)) {
//                    Toast.makeText(v.getContext(), "The specified parameter accessKeyId cannot be null", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (StringUtil.isEmpty(accessKeySecret)) {
//                    Toast.makeText(v.getContext(), "The specified parameter \"accessKeySecret\" cannot be null", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (StringUtil.isEmpty(securityToken)) {
//                    Toast.makeText(v.getContext(), "The specified parameter \"securityToken\" cannot be null", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (StringUtil.isEmpty(expriedTime)) {
//                    Toast.makeText(v.getContext(), "The specified parameter \"expriedTime\" cannot be null", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (!(new File(videoPath)).exists()) {
//                    Toast.makeText(v.getContext(), "The specified parameter \"videoPath\" file not exists", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (!(new File(imagePath)).exists()) {
//                    Toast.makeText(v.getContext(), "The specified parameter \"imagePath\" file not exists", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                //参数请确保存在，如不存在SDK内部将会直接将错误throw Exception
//                // 文件路径保证存在之外因为Android 6.0之后需要动态获取权限，请开发者自行实现获取"文件读写权限".
//                VodHttpClientConfig vodHttpClientConfig = new VodHttpClientConfig.Builder()
//                .setMaxRetryCount(2)
//                .setConnectionTimeout(15 * 1000)
//                .setSocketTimeout(15 * 1000)
//                .build();
//
//                SvideoInfo svideoInfo = new SvideoInfo();
//                svideoInfo.setTitle(new File(videoPath).getName());
//                svideoInfo.setDesc("");
//                svideoInfo.setCateId(1);
//
//                VodSessionCreateInfo vodSessionCreateInfo = new  VodSessionCreateInfo.Builder()
//                .setImagePath(imagePath)
//                .setVideoPath(videoPath)
//                .setAccessKeyId(accessKeyId)
//                .setAccessKeySecret(accessKeySecret)
//                .setSecurityToken(securityToken)
//                .setRequestID(requestID)
//                .setExpriedTime(expriedTime)
//                .setIsTranscode(true)
//                .setStorageLocation("outin-12ebe01f029d11e9b63300163e1c8dba.oss-cn-shanghai.aliyuncs.com") //out-20170320144514766-asatv1s154.oss-cn-shanghai.aliyuncs.com   //in-20170320144514766-hte7xhqk1t.oss-cn-shanghai.aliyuncs.com
//                .setTemplateGroupId("3e83f44f870ae653ff6516156af77478")
//                .setSvideoInfo(svideoInfo)
//                .setPartSize(1024 * 1024) // 1M
//                .setVodHttpClientConfig(vodHttpClientConfig)
//                .build();
//                vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
//                    @Override
//                    public void onUploadSucceed(String videoId, String imageUrl) {
//                        Log.d(TAG, "onUploadSucceed" + "videoId:" + videoId + "imageUrl" + imageUrl);
//                        progress = 100;
//                        handler.sendEmptyMessage(0);
//                    }
//
//                    @Override
//                    public void onUploadFailed(String code, String message) {
//                        Log.d(TAG, "onUploadFailed" + "code" + code + "message" + message);
//                        progress = -1;
//                        handler.sendEmptyMessage(0);
//                    }
//
//                    @Override
//                    public void onUploadProgress(long uploadedSize, long totalSize) {
//                        Log.d(TAG, "onUploadProgress - " + uploadedSize * 100 / totalSize + "%");
//                        progress = uploadedSize * 100 / totalSize;
//                        handler.sendEmptyMessage(0);
//                    }
//
//                    @Override
//                    public void onSTSTokenExpried() {
//                        Log.d(TAG, "onSTSTokenExpried");
//                        vodsVideoUploadClient.refreshSTSToken(accessKeyId, accessKeySecret, securityToken, expriedTime);
//                    }
//
//                    @Override
//                    public void onUploadRetry(String code, String message) {
//                        //上传重试的提醒
//                        Log.d(TAG, "onUploadRetry" + "code" + code + "message" + message);
//                    }
//
//                    @Override
//                    public void onUploadRetryResume() {
//                        //上传重试成功的回调.告知用户重试成功
//                        Log.d(TAG, "onUploadRetryResume");
//                    }
//                });
//            }
//        });
//
//        btnCancel = (Button) findViewById(R.id.btn_cancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vodsVideoUploadClient.cancel();
//                progress = 0;
//                handler.sendEmptyMessage(0);
//                Log.d(TAG, "onUpload " + "canceled");
//            }
//        });
//        btnResume = (Button) findViewById(R.id.btn_resume);
//        btnResume.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vodsVideoUploadClient.resume();
//            }
//        });
//
//        btnPause = (Button) findViewById(R.id.btn_pause);
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vodsVideoUploadClient.pause();
//            }
//        });
//
//        tvProgress = (TextView) findViewById(R.id.tv_progress);
//
//        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this.getApplicationContext());
//        vodsVideoUploadClient.init();
//        vodsVideoUploadClient.setRegion(VOD_REGION);
//        vodsVideoUploadClient.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED);
//    }
//
//    private void getIntentExtra() {
//
//        accessKeyId = getIntent().getStringExtra("accessKeyId");
//        accessKeySecret = getIntent().getStringExtra("accessKeySecret");
//        securityToken = getIntent().getStringExtra("securityToken");
//        expriedTime = getIntent().getStringExtra("expiration");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        vodsVideoUploadClient.resume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        vodsVideoUploadClient.pause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        vodsVideoUploadClient.release();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    // UI只允许在主线程更新。
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            //更新进度
//            tvProgress.setText("进度：" + String.valueOf(progress) );
//        }
//    };
//
//    private boolean isCreateFile;
//    private AsyncTask getFileTask = new AsyncTask() {
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            videoPath = FileUtils.createFileFromInputStream(SVideoUploadActivity.this, "aliyunmedia.mp4").getPath();
//            imagePath = FileUtils.createFileFromInputStream(SVideoUploadActivity.this, "001.jpg").getPath();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            isCreateFile = true;
//            super.onPostExecute(o);
//        }
//    };
//}
