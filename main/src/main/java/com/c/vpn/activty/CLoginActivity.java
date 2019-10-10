package com.c.vpn.activty;

import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c.vpn.common.Url;
import com.c.vpn.model.AutoLoginEvent;
import com.c.vpn.model.ExitLoginActivityEvent;
import com.c.vpn.model.LoginEvent;
import com.c.vpn.model.SaltEvent;
import com.c.vpn.model.UserInfoModel;
import com.c.vpn.utill.CommmonUtil;
import com.c.vpn.utill.HttpUtil;
import com.c.vpn.utill.SaltUtil;
import com.c.vpn.utill.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.utils.HttpUtils;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleCallback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

public class CLoginActivity extends CBaseActivity implements View.OnClickListener {
    private EditText etAccount;
    private EditText etPassword;
    public static final int LOGIN_EXIT = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clogin);
        initActionBar();
        initViews();
        TrafficStats.setThreadStatsTag(12000);
    }


    private void initViews() {
        findViewById(R.id.tv_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CLoginActivity.this,CForgetPassWordActivity.class);
                startActivityForResult(intent,LOGIN_EXIT);
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(this);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.tiet_password);
    }

    @Override
    public void initActionBar() {
        super.initActionBar();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        final MenuItem item = menu.findItem(R.id.action_edit);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CLoginActivity.this,CRegActivity.class);
                startActivityForResult(intent,LOGIN_EXIT);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == LOGIN_EXIT){
            goToMain();
        }
    }

    public void goToMain(){
        Intent intent = new Intent(CLoginActivity.this,CMainActivity.class);
        startActivity(intent);
        finish();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        JSONObject obj = event.data;
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
        }else{
            String username =etAccount.getText().toString();
            String password = etPassword.getText().toString();
            CommmonUtil.saveId(username,this);
            CommmonUtil.savePassword(password,this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAutoLogin(AutoLoginEvent event) {

    }

    @Override
    public void onClick(View view) {
        String username =etAccount.getText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtils.showToastLong(CLoginActivity.this,"邮箱不能为空");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject result = HttpUtil.sendGet(Url.SALT+"?id="+username);
                if(result != null){
                    EventBus.getDefault().post(new SaltEvent(result));
                }
            }
        }).start();
    }


    public void login(String salt){
        String username =etAccount.getText().toString();
        String password = etPassword.getText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtils.showToastLong(CLoginActivity.this,"邮箱不能为空");
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtils.showToastLong(CLoginActivity.this,"密码不能为空");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject result = HttpUtil.sendGet(Url.LOGIN+"?id="+username+"&password="+SaltUtil.makeSalt(password,salt));
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
