//package com.alibaba.sdk.android.vodupload_demo.old;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.alibaba.sdk.android.oss.common.OSSLog;
//import com.alibaba.sdk.android.vod.upload.ResumableVODUploadCallback;
//import com.alibaba.sdk.android.vod.upload.VODUploadClient;
//import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
//import com.alibaba.sdk.android.vod.upload.common.UploadStateType;
//import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
//import com.alibaba.sdk.android.vod.upload.model.VodInfo;
//import com.alibaba.sdk.android.vod.upload.model.VodUploadResult;
//import com.alibaba.sdk.android.vodupload_demo.FileUtils;
//import com.alibaba.sdk.android.vodupload_demo.ItemInfo;
//import com.alibaba.sdk.android.vodupload_demo.R;
//import com.alibaba.sdk.android.vodupload_demo.VODUploadAdapter;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
///**
// * 点播上传演示示例
// * 功能点：
// * 1.多文件队列上传
// * 2.开发者仅需要管理STS的获取，需要关注OSS，MTS的相关信息
// * 3.支持转码属性指定
// * 4.
// * Created by Mulberry on 2018/1/4.
// */
//@Deprecated
////public class VodMultiUploadActivity extends AppCompatActivity {
//
//    private String accessKeyId = "";
//    private String accessKeySecret = "";
//    private String secretToken = "";
//    private String expireTime = "";
//
//    private int index = 0;
//    private Random random = new Random();
//
//    private Handler handler;
//    VODUploadClient uploader;
//    private VODUploadAdapter vodUploadAdapter;
//    public static String VOD_REGION = "cn-shanghai";
//    public static boolean VOD_RECORD_UPLOAD_PROGRESS_ENABLED = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.multi_upload);
//
//
//        getIntentExtra();
//
//        List<ItemInfo> list = new ArrayList<>();
//        vodUploadAdapter = new VODUploadAdapter(VodMultiUploadActivity.this,
//                                                R.layout.listitem, list);
//
//        final ListView listView = (ListView) findViewById(R.id.listView);
//        listView.setAdapter(vodUploadAdapter);
//
//        // 打开日志。
//        OSSLog.enableLog();
//
//        // UI只允许在主线程更新。
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                vodUploadAdapter.notifyDataSetChanged();
//            }
//        };
//
//
//        uploader = new VODUploadClientImpl(getApplicationContext());
//        uploader.setRegion(VOD_REGION);
//        uploader.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED);
//        ResumableVODUploadCallback callback = new ResumableVODUploadCallback() {
//
//            @Override
//            public void onUploadFinished(UploadFileInfo info, VodUploadResult result) {
//                super.onUploadFinished(info, result);
//                for (int i = 0; i < vodUploadAdapter.getCount(); i++) {
//                    if (vodUploadAdapter.getItem(i).getFile().equals(info.getFilePath())) {
//                        vodUploadAdapter.getItem(i).setProgress(100);
//                        vodUploadAdapter.getItem(i).setStatus(info.getStatus().toString());
//                        handler.sendEmptyMessage(0);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onUploadFailed(UploadFileInfo info, String code, String message) {
//                OSSLog.logError("onfailed ------------------ " + info.getFilePath() + " " + code + " " + message);
//                for (int i = 0; i < vodUploadAdapter.getCount(); i++) {
//                    if (vodUploadAdapter.getItem(i).getFile().equals(info.getFilePath())) {
//                        vodUploadAdapter.getItem(i).setProgress(-1);
//                        vodUploadAdapter.getItem(i).setStatus(info.getStatus().toString());
//                        handler.sendEmptyMessage(0);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
//                OSSLog.logDebug("onProgress ------------------ " + info.getFilePath() + " " + uploadedSize + " " + totalSize);
//                for (int i = 0; i < vodUploadAdapter.getCount(); i++) {
//                    if (vodUploadAdapter.getItem(i).getStatus() == String.valueOf(UploadStateType.SUCCESS)
//                            || vodUploadAdapter.getItem(i).getStatus() == String.valueOf(UploadStateType.CANCELED)) {
//                        continue;
//                    }
//                    if (vodUploadAdapter.getItem(i).getFile().equals(info.getFilePath())) {
//                        vodUploadAdapter.getItem(i).setProgress(uploadedSize * 100 / totalSize);
//                        vodUploadAdapter.getItem(i).setStatus(info.getStatus().toString());
//                        handler.sendEmptyMessage(0);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onUploadTokenExpired() {
//                OSSLog.logError("onExpired ------------- ");
//                //刷新STS:实现时，重新获取STS临时账号用于恢复上传
//                try {
//                    getVodUploadInfo();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onUploadRetry(String code, String message) {
//                OSSLog.logError("onUploadRetry ------------- ");
//            }
//        };
//
//        uploader.init(accessKeyId, accessKeySecret, secretToken, expireTime, callback);
//        uploader.setPartSize(1024 * 1024);
//
//        Button btnAdd = (Button) findViewById(R.id.btn_addFile);
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isCreatingFile) {
//                    Toast.makeText(v.getContext(), "Video does is copy，Please wiat!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(accessKeyId) || TextUtils.isEmpty(accessKeySecret) ||
//                        TextUtils.isEmpty(secretToken) || TextUtils.isEmpty(expireTime)) {
//                    Toast.makeText(v.getContext(), "OSS上传请务必保证STS(accessKeyId/accessKeySecret/secretTokenexpireTime)信息不为空！", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                // 保证本地文件地址存在，如果文件不存在，拷贝一个临时文件，正式环境替换用户自己的视频
//                getFileTask().execute();
//
//            }
//        });
//
//        Button btnDelete = (Button) findViewById(R.id.btn_deleteFile);
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (uploader == null) {
//                    return;
//                }
//
//                if (vodUploadAdapter.getCount() == 0) {
//                    Context context = getApplicationContext();
//                    Toast.makeText(context, "列表为空啦!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                int index = uploader.listFiles().size() - 1;
//                UploadFileInfo info = uploader.listFiles().get(index);
//                uploader.deleteFile(index);
//                OSSLog.logDebug("删除了一个文件：" + info.getFilePath());
//
//                vodUploadAdapter.remove(vodUploadAdapter.getItem(index));
//
//                for (UploadFileInfo uploadFileInfo : uploader.listFiles()) {
//                    OSSLog.logDebug("file path:" + uploadFileInfo.getFilePath() +
//                                    ", status:" + uploadFileInfo.getStatus());
//
//                }
//            }
//        });
//
//        Button btnCancel = (Button) findViewById(R.id.btn_cancelFile);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (uploader == null) {
//                    return;
//                }
//                Context context = getApplicationContext();
//
//                int index = uploader.listFiles().size() - 1;
//                if (index < 0) {
//                    Toast.makeText(context, "请先添加文件再执行取消操作.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Toast.makeText(context, "取消文件上传", Toast.LENGTH_SHORT).show();
//                UploadFileInfo info = uploader.listFiles().get(index);
//                uploader.cancelFile(index);
//
//                if (vodUploadAdapter.getItem(index).getStatus() != String.valueOf(UploadStateType.CANCELED)) {
//                    vodUploadAdapter.getItem(index).setStatus(info.getStatus().toString());
//                    handler.sendEmptyMessage(0);
//                }
//
////                for(int i=0; i<vodUploadAdapter.getCount(); i++) {
////                    if (vodUploadAdapter.getItem(i).getFile().equals(info.getFilePath())) {
////                        vodUploadAdapter.getItem(i).setStatus(info.getStatus().toString());
////                        handler.sendEmptyMessage(0);
////                        break;
////                    }
////                }
//
//                return;
//            }
//        });
//
//        Button btnResumeFile = (Button) findViewById(R.id.btn_resumeFile);
//        btnResumeFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (uploader == null) {
//                    return;
//                }
//
//                Context context = getApplicationContext();
//                if (uploader.listFiles().size() <= 0) {
//                    Toast.makeText(context, "请先添加文件再执行恢复操作.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Toast.makeText(context, "恢复文件上传", Toast.LENGTH_SHORT).show();
//                for (int i = 0; i < uploader.listFiles().size(); i++) {
//                    UploadFileInfo info = uploader.listFiles().get(i);
//                    if (info.getStatus() == UploadStateType.CANCELED || info.getStatus() == UploadStateType.FAIlURE) {
//                        uploader.resumeFile(i);
//                        vodUploadAdapter.getItem(i).setStatus(uploader.listFiles().get(i).getStatus().toString());
//                    }
//                }
//                handler.sendEmptyMessage(0);
//            }
//        });
//
//        Button btnList = (Button) findViewById(R.id.btn_getList);
//        btnList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//                Toast.makeText(context, "获取文件列表，" + uploader.listFiles().size(),
//                               Toast.LENGTH_SHORT).show();
//
//                for (UploadFileInfo uploadFileInfo : uploader.listFiles()) {
//                    OSSLog.logDebug("file path:" + uploadFileInfo.getFilePath() +
//                                    ", status:" + uploadFileInfo.getStatus());
//                }
//                return;
//            }
//        });
//
//        Button btnClear = (Button) findViewById(R.id.btn_clearList);
//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//
//                uploader.clearFiles();
//
//                Toast.makeText(context, "清理文件列表完成。", Toast.LENGTH_SHORT).show();
//                vodUploadAdapter.clear();
//                OSSLog.logDebug("列表大小为：" + uploader.listFiles().size());
//
//                return;
//            }
//        });
//
//        Button btnStart = (Button) findViewById(R.id.btn_start);
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//                Toast.makeText(context, "开始上传", Toast.LENGTH_SHORT).show();
//
//                uploader.start();
//            }
//        });
//
//        Button btnStop = (Button) findViewById(R.id.btn_stop);
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//                Toast.makeText(context, "停止上传", Toast.LENGTH_SHORT).show();
//
//                uploader.stop();
//            }
//        });
//
//        Button btnPause = (Button) findViewById(R.id.btn_pause);
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//                Toast.makeText(context, "暂停上传", Toast.LENGTH_SHORT).show();
//
//                uploader.pause();
//                return;
//            }
//        });
//
//        Button btnResume = (Button) findViewById(R.id.btn_resume);
//        btnResume.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = getApplicationContext();
//                Toast.makeText(context, "恢复上传", Toast.LENGTH_SHORT).show();
//
//                uploader.resume();
//                return;
//            }
//        });
//    }
//
//    private VodInfo getVodInfo() {
//        VodInfo vodInfo = new VodInfo();
//        vodInfo.setTitle("标题" + index);
//        vodInfo.setDesc("描述." + index);
//        vodInfo.setCateId(index);
//        vodInfo.setIsProcess(true);
//        vodInfo.setCoverUrl("http://www.taobao.com/" + index + ".jpg");
//        List<String> tags = new ArrayList<>();
//        tags.add("标签" + index);
//        vodInfo.setTags(tags);
//        vodInfo.setIsShowWaterMark(false);
//        vodInfo.setPriority(7);
//        vodInfo.setUserData("自定义数据" + index);
//        return vodInfo;
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
//    private void getIntentExtra() {
//        accessKeyId = getIntent().getStringExtra("accessKeyId");
//        accessKeySecret = getIntent().getStringExtra("accessKeySecret");
//        secretToken = getIntent().getStringExtra("securityToken");
//        expireTime = getIntent().getStringExtra("expiration");
//    }
//
//    private boolean isCreatingFile;
//    private String videoPath;
//
//    private AsyncTask getFileTask() {
//        return new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                isCreatingFile = true;
//                videoPath = FileUtils.createFileFromInputStream(VodMultiUploadActivity.this, "aliyunmedia.mp4", "" + index).getPath();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                isCreatingFile = false;
//                uploader.addFile(videoPath, getVodInfo());
//                OSSLog.logDebug("添加了一个文件：" + videoPath);
//                // 获取刚添加的文件。
//                UploadFileInfo uploadFileInfo = uploader.listFiles().get(uploader.listFiles().size() - 1);
//
//                // 添加到列表。
//                ItemInfo info = new ItemInfo();
//                info.setFile(uploadFileInfo.getFilePath());
//                info.setProgress(0);
//                info.setStatus(uploadFileInfo.getStatus().toString());
//                vodUploadAdapter.add(info);
//
//                index++;
//
//            }
//        };
//    }
//
//    public void getVodUploadInfo() throws Exception {
//        OkHttpClient client;
//        client = new OkHttpClient.Builder()
//        .build();
//        Request request = new Request.Builder()
//        .url("https://demo-vod.cn-shanghai.aliyuncs.com/voddemo/CreateSecurityToken" +
//             "?BusinessType=vodai" +
//             "&TerminalType=iphone" +
//             "&DeviceModel=FD394CE2-90EE-4D35-80A6-1A32D8FB77C5" +
//             "&UUID=x86_64" +
//             "&AppVersion=1.0.0")
//        .build();
//
//        client.newCall(request).enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                ResponseBody responseBody = response.body();
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//
//                Headers responseHeaders = response.headers();
//                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                }
//                try {
//
//                    JSONObject jsonObject = new JSONObject(responseBody.string());
//                    JSONObject securityTokenInfo = jsonObject.optJSONObject("SecurityTokenInfo");
//
//                    accessKeyId = securityTokenInfo.optString("AccessKeyId");
//                    accessKeySecret = securityTokenInfo.optString("AccessKeySecret");
//                    secretToken = securityTokenInfo.optString("SecurityToken");
//                    expireTime = securityTokenInfo.optString("Expiration");
//
//                    uploader.resumeWithToken(accessKeyId, accessKeySecret, secretToken, expireTime);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//}
