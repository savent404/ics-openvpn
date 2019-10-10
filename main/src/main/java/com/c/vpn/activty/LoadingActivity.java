package com.c.vpn.activty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c.vpn.common.Url;
import com.c.vpn.model.LoginEvent;
import com.c.vpn.model.SaltEvent;
import com.c.vpn.model.UserInfoModel;
import com.c.vpn.utill.CommmonUtil;
import com.c.vpn.utill.HttpUtil;
import com.c.vpn.utill.SaltUtil;
import com.c.vpn.utill.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

public class LoadingActivity extends AppCompatActivity {
    String id;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        checkLogin();
    }

    private void checkLogin(){
         id = CommmonUtil.getID(this);
         pwd = CommmonUtil.getPassword(this);
        if(!TextUtils.isEmpty(id)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = HttpUtil.sendGet(Url.SALT+"?id="+id);
                    if(result != null){
                        EventBus.getDefault().post(new SaltEvent(result));
                    }
                }
            }).start();
        }else{
           goToLogin();
        }
    }


    public void goToLogin(){
        Intent intent = new Intent(LoadingActivity.this,CLoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMain(){
        Intent intent = new Intent(LoadingActivity.this,CMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        JSONObject obj = event.data;
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
            CommmonUtil.saveId("",this);
            CommmonUtil.savePassword("",this);
            goToLogin();
        }else{
            UserInfoModel userInfoModel = JSON.parseObject(JSON.toJSONString(obj),UserInfoModel.class);
            ICSOpenVPNApplication application = (ICSOpenVPNApplication) getApplication();
            application.setUserInfo(userInfoModel);
            goToMain();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSalt(SaltEvent event) {
        JSONObject obj = event.data;
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
        }else{
            String salt = obj.getString("salt");
            login(salt);
        }
    }


    public void login(String salt){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject result = HttpUtil.sendGet(Url.LOGIN+"?id="+id+"&password="+ SaltUtil.makeSalt(pwd,salt));
                if(result != null){
                    EventBus.getDefault().post(new LoginEvent(result));
                }
            }
        }).start();
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
