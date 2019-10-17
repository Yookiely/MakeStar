//package com.alibaba.sdk.android.vodupload_demo.old;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.alibaba.sdk.android.vod.upload.auth.AliyunVodAuth;
//import com.alibaba.sdk.android.vod.upload.exception.VODClientException;
//import com.alibaba.sdk.android.vodupload_demo.R;
//import com.aliyun.auth.common.AliyunVodUploadType;
//import com.aliyun.auth.model.CreateImageForm;
//import com.aliyun.auth.model.CreateVideoForm;
//import com.aliyun.vod.common.httpfinal.QupaiHttpFinal;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLSession;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//import static com.alibaba.sdk.android.vodupload_demo.MainActivity.UPLOAD_TYPE_OSSMULTIPE;
//import static com.alibaba.sdk.android.vodupload_demo.MainActivity.UPLOAD_TYPE_SVIDEO;
//import static com.alibaba.sdk.android.vodupload_demo.MainActivity.UPLOAD_TYPE_VODMULTIPLE;
//
///**
// * Created by Mulberry on 2018/1/5.
// * sts上传方式已不再维护，推荐使用点播凭证方式上传
// */
//
//@Deprecated
//public class GetSTStokenActivity extends AppCompatActivity implements View.OnClickListener {
//
//    Button btnGetSTS;
//    TextView tvSts;
//    private  OkHttpClient client;
//    private int uploadType;
//    AliyunVodAuth aliyunVodAuth;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        QupaiHttpFinal.getInstance().initOkHttpFinal();
//        uploadType = getIntent().getIntExtra("UploadType", 0);
//
//        client = new OkHttpClient.Builder()
//        .hostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return "alivc-demo.aliyuncs.com".equals(hostname);
//            }
//        })
//        .build();
//        setContentView(R.layout.get_sts_layout);
//        btnGetSTS = (Button) findViewById(R.id.btn_getsts);
//        tvSts = (TextView) findViewById(R.id.tv_sts);
//        btnGetSTS.setOnClickListener(this);
//
//        this.aliyunVodAuth = new AliyunVodAuth(new AliyunAuthCallback());
//    }
//
//    @Override
//    public void onClick(View v) {
//        try {
//            if (uploadType == UPLOAD_TYPE_OSSMULTIPE) {
//                getOSSUploadInfo();
//            } else {
//                getVodUploadInfo();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    String accessKeyId;
//    String accessKeySecret;
//    String securityToken;
//    String expiration;
//
//    String endpoint;
//    String bucket;
//    String objectKey;
//    String prefix;
//
//    public void getVodUploadInfo() throws Exception {
//        Request request = new Request.Builder()
//        .url("https://alivc-demo.aliyuncs.com/demo/getSts")
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
//                    JSONObject securityTokenInfo = jsonObject.optJSONObject("data");
//
//                    accessKeyId =  securityTokenInfo.optString("accessKeyId");
//                    accessKeySecret =  securityTokenInfo.optString("accessKeySecret");
//                    securityToken =  securityTokenInfo.optString("securityToken");
//                    expiration =  securityTokenInfo.optString("expiration");
//
//                    handler.sendEmptyMessage(1);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void getOSSUploadInfo() {
//        Request request = new Request.Builder()
//        .url("https://alivc-demo.aliyuncs.com/demo/getVideoUploadAuth?fileName=media/xxx.mp4&title=xxx")
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
//                    JSONObject jsonObject = new JSONObject(responseBody.string());
//                    JSONObject data = jsonObject.getJSONObject("data");
//                    String uploadAddress =  data.optString("uploadAddress");
//                    String securityTokenInfo =  data.optString("uploadAuth");
//                    if (uploadAddress!=null){
//                        byte[] addressJsonBytes = Base64.decode(uploadAddress, Base64.DEFAULT);
//                        uploadAddress = new String(addressJsonBytes);
//                    }
//                    if (securityTokenInfo!=null){
//                        byte[] authJsonBytes = Base64.decode(securityTokenInfo, Base64.DEFAULT);
//                        securityTokenInfo = new String(authJsonBytes);
//                    }
//                    JSONObject addressKey;
//                    try {
//                        addressKey = new JSONObject(securityTokenInfo);
//                        accessKeyId = addressKey.optString("AccessKeyId");
//                        accessKeySecret = addressKey.optString("AccessKeySecret");
//                        securityToken = addressKey.optString("SecurityToken");
//                        expiration = addressKey.optString("Expiration");
//                    } catch (JSONException var9) {
//                        throw new VODClientException("MissingArgument", "The specified parameter \"uploadAuth\" format is error");
//                    }
//
//                    try {
//                        addressKey = new JSONObject(uploadAddress);
//                        endpoint = addressKey.optString("Endpoint");
//                        bucket = addressKey.optString("Bucket");
//                        String fileName = addressKey.optString("FileName");
//                        if (!TextUtils.isEmpty(fileName)){
//                            prefix = fileName.substring(0,fileName.indexOf("/"));
//                        }
//                    } catch (JSONException var8) {
//                        throw new VODClientException("MissingArgument", "The specified parameter \"uploadAddress\" format is error");
//                    }
//
//                    handler.sendEmptyMessage(1);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//
//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            tvSts.setText("\nAccessKeyId:" + accessKeyId + "\naccessKeySecret:" + accessKeySecret  +
//                          "\nsecurityToken:" + securityToken + "\nexpiration:" +  expiration);
//            startActivity(uploadType);
//            return false;
//        }
//    });
//
//    private void startActivity(int type) {
//        switch (type) {
//        case UPLOAD_TYPE_VODMULTIPLE:
//            Intent vodintent = new Intent();
//            vodintent.setClass(GetSTStokenActivity.this, VodMultiUploadActivity.class);
//            vodintent.putExtra("accessKeyId", accessKeyId);
//            vodintent.putExtra("accessKeySecret", accessKeySecret);
//            vodintent.putExtra("securityToken", securityToken);
//            vodintent.putExtra("expiration", expiration);
//            startActivity(vodintent);
//            break;
//        case UPLOAD_TYPE_SVIDEO:
//            Intent svideointent = new Intent();
//            svideointent.setClass(GetSTStokenActivity.this, SVideoUploadActivity.class);
//            svideointent.putExtra("accessKeyId", accessKeyId);
//            svideointent.putExtra("accessKeySecret", accessKeySecret);
//            svideointent.putExtra("securityToken", securityToken);
//            svideointent.putExtra("expiration", expiration);
//            startActivity(svideointent);
//            break;
//        case UPLOAD_TYPE_OSSMULTIPE:
//            Intent ossintent = new Intent();
//            ossintent.setClass(GetSTStokenActivity.this, OSSMultiUploadActivity.class);
//            ossintent.putExtra("accessKeyId", accessKeyId);
//            ossintent.putExtra("accessKeySecret", accessKeySecret);
//            ossintent.putExtra("securityToken", securityToken);
//            ossintent.putExtra("expiration", expiration);
//            ossintent.putExtra("Endpoint", endpoint);
//            ossintent.putExtra("Bucket", bucket);
//            ossintent.putExtra("Prefix", prefix);
//            startActivity(ossintent);
//            break;
//        default:
//            break;
//        }
//
//    }
//
//    class AliyunAuthCallback implements AliyunVodAuth.VodAuthCallBack {
//        @Override
//        public void onCreateUploadVideoed(CreateVideoForm createVideoForm, String s) {
//            String addressJsonString;
//            JSONObject addressKey;
//            byte[] addressJsonBytes;
//            try {
//                addressJsonBytes = Base64.decode(createVideoForm.getUploadAuth(), 0);
//                addressJsonString = new String(addressJsonBytes);
//                addressKey = new JSONObject(addressJsonString);
//                accessKeyId = addressKey.optString("AccessKeyId");
//                accessKeySecret = addressKey.optString("AccessKeySecret");
//                securityToken = addressKey.optString("SecurityToken");
//                expiration = addressKey.optString("Expiration");
//            } catch (JSONException var9) {
//                throw new VODClientException("MissingArgument", "The specified parameter \"uploadAuth\" format is error");
//            }
//
//            try {
//                addressJsonBytes = Base64.decode(createVideoForm.getUploadAddress(), 0);
//                addressJsonString = new String(addressJsonBytes);
//                addressKey = new JSONObject(addressJsonString);
//                endpoint = addressKey.optString("Endpoint");
//                bucket = addressKey.optString("Bucket");
//                objectKey = addressKey.optString("FileName");
//
//            } catch (JSONException var8) {
//                throw new VODClientException("MissingArgument", "The specified parameter \"uploadAddress\" format is error");
//            }
//
//            handler.sendEmptyMessage(1);
//        }
//
//        @Override
//        public void onCreateUploadImaged(CreateImageForm createImageForm) {
//
//        }
//
//        @Override
//        public void onError(String s, String s1) {
//            Log.d("GetSTS", "Code" + s + "message" + s1);
//        }
//
//        @Override
//        public void onSTSExpired(AliyunVodUploadType aliyunVodUploadType) {
//
//        }
//    }
//
//
//}
