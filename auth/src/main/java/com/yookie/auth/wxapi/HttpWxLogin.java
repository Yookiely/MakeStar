//package com.yookie.auth.wxapi;
//
//import android.util.Log;
//
//import com.google.gson.Gson;
//
//import java.io.IOException;
//import java.utail.HashMap;
//import java.util.Map;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class HttpWxLogin {
//    private String openid;
//
//    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    private ReturnHttpResult returnHttpResult;
//
//    public HttpWxLogin(String openid) {
//        this.openid = openid;
//    }
//
//    public void saveLoginWx() {
//        OkHttpClient client = new OkHttpClient();
//
//        Map<String, String> map = new HashMap<>();
//        map.put("wxid", openid);
//        Gson gson = new Gson();
//        String params = gson.toJson(map);
//
//        RequestBody requestBody = RequestBody.create(JSON, params);
//        Request request = new Request.Builder()
//                .post(requestBody)
//                .url("自己项目的登录逻辑地址")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("WXEntryActivityLog", "wxlogin: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String data = response.body().string();
//                Log.d("WXEntryActivityLog", "wxlogin:得到的返回数据 " + data);
//                returnHttpResult.clickReturnHttpResult(data);
//            }
//        });
//    }
//
//    public void setReturnHttpResult(ReturnHttpResult returnHttpResult) {
//        this.returnHttpResult = returnHttpResult;
//    }
//
//}
