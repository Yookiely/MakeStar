package com.yookie.auth;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseUiListener implements IUiListener {
    //注意测试需要在腾讯开放平台注册调试者QQ号
    @Override
    public void onComplete(Object response) {
        try {

            String openId = ((JSONObject) response).getString("openid");
            String expires = ((JSONObject) response).getString("expires_in");
            String token = ((JSONObject) response).getString("access_token");
            Log.d("ddd",((JSONObject) response).getString("access_token"));
            EventBus.getDefault().post(response);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(UiError e) {

    }

    @Override
    public void onCancel() {

    }

}
