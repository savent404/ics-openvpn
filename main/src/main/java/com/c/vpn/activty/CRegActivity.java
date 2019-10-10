package com.c.vpn.activty;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c.vpn.common.Url;
import com.c.vpn.model.AutoLoginEvent;
import com.c.vpn.model.ExitLoginActivityEvent;
import com.c.vpn.model.LoginEvent;
import com.c.vpn.model.RegCodeEvent;
import com.c.vpn.model.RegEvent;
import com.c.vpn.model.RegLoginEvent;
import com.c.vpn.model.SaltEvent;
import com.c.vpn.model.ServerListEvent;
import com.c.vpn.model.UserInfoModel;
import com.c.vpn.utill.CommmonUtil;
import com.c.vpn.utill.HttpAsyncRequest;
import com.c.vpn.utill.HttpUtil;
import com.c.vpn.utill.SaltUtil;
import com.c.vpn.utill.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.activities.BaseActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

public class CRegActivity extends CBaseActivity {
    private TextView tvCode;
    private EditText etUsername;
    private EditText etCode;
    private EditText etPassword;

    private Button btnReg;
    private TimeCount time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creg);
        initActionBar();
        initViews();
    }

    private void initViews() {
        tvCode = findViewById(R.id.tv_getCode);
        etUsername = findViewById(R.id.et_account);
        etCode = findViewById(R.id.et_code);
        etPassword = findViewById(R.id.tiet_password);
        btnReg = findViewById(R.id.btn_reg);
        time = new TimeCount(60000, 1000);
        tvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                if(TextUtils.isEmpty(username)){
                    ToastUtils.showToastLong(CRegActivity.this,"邮箱不能为空");
                    return;
                }
                if(!CommmonUtil.isEmail(username)){
                    ToastUtils.showToastLong(CRegActivity.this,"请填写正确的邮箱格式");
                    return;
                }
                requestVerifyCode task = new requestVerifyCode();
                task.request(username);
                time.start();
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                if(TextUtils.isEmpty(username)){
                    ToastUtils.showToastLong(CRegActivity.this,"邮箱不能为空");
                    return;
                }
                if(!CommmonUtil.isEmail(username)){
                    ToastUtils.showToastLong(CRegActivity.this,"请填写正确的邮箱格式");
                    return;
                }

                String code = etCode.getText().toString();
                if(TextUtils.isEmpty(code)){
                    ToastUtils.showToastLong(CRegActivity.this,"验证码不能为空");
                    return;
                }
                String password = etPassword.getText().toString();
                if(TextUtils.isEmpty(password)){
                    ToastUtils.showToastLong(CRegActivity.this,"密码不能为空");
                    return;
                }
                if(!CommmonUtil.checkPasswordRule(password)){
                    ToastUtils.showToastLong(CRegActivity.this,"密码必须包含大小写字母及数字且不低于6位");
                    return;
                }
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject result = HttpUtil.sendGet(Url.REGISTER+"?id="+username+"&verify="+code+"&password="+password);
                        if(result != null){
                            EventBus.getDefault().post(new RegEvent(result));
                        }
                        dissmissDialog();
                    }
                }).start();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReg(RegEvent event) {
        JSONObject obj = event.data;
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
        }else{
            ToastUtils.showToastLong(CRegActivity.this,"注册成功");
            //这里自动登录逻辑
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = HttpUtil.sendGet(Url.SALT+"?id="+etUsername.getText().toString());
                    if(result.getIntValue("code") != 0){
                        ToastUtils.showOnUIThreadx(CRegActivity.this,result.getString("desc"));
                    }else{
                        String salt = result.getString("salt");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject result = HttpUtil.sendGet(Url.LOGIN+"?id="+etUsername.getText().toString()+"&password="+ SaltUtil.makeSalt(etPassword.getText().toString(),salt));
                                if(result != null){
                                    if(result.getIntValue("code") != 0){
                                        ToastUtils.showOnUIThreadx(CRegActivity.this,result.getString("desc"));
                                    }else{
                                        EventBus.getDefault().post(new RegLoginEvent(result));
                                    }
                                }
                            }
                        }).start();
                    }
                }
            }).start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(RegLoginEvent event) {
        JSONObject obj = event.data;
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
        }else{
            String username =etUsername.getText().toString();
            String password = etPassword.getText().toString();
            CommmonUtil.saveId(username,this);
            CommmonUtil.savePassword(password,this);
            UserInfoModel userInfoModel = JSON.parseObject(JSON.toJSONString(obj),UserInfoModel.class);
            ICSOpenVPNApplication application = (ICSOpenVPNApplication) getApplication();
            application.setUserInfo(userInfoModel);
            goToMain();
        }
    }

    public void goToMain(){
        Intent i = new Intent();
        setResult(CLoginActivity.LOGIN_EXIT, i);
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCode(RegCodeEvent event) {
        JSONObject obj = event.data;
        if(obj == null){
            ToastUtils.showToastLong(this, "发送邮件失败");
            return;
        }
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
        }else{
            ToastUtils.showToastLong(this, "发送邮件成功");
        }
    }


    public class requestVerifyCode implements HttpAsyncRequest.HttpAsyncCallback {

        public void request (String username) {
            HttpAsyncRequest task = new HttpAsyncRequest(6000, 60000, this);
            task.execute(Url.CODE+"?id="+username+"&method=0");
        }
        @Override
        public void completionHandler(Boolean success, JSONObject obj) {
            if (success) {
                EventBus.getDefault().post(new RegCodeEvent(obj));
            } else {
                EventBus.getDefault().post(new RegCodeEvent(null));
            }
        }
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

    @Override
    public void initActionBar() {
        super.initActionBar();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCode.setClickable(false);
            tvCode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            tvCode.setText("重新获取验证码");
            tvCode.setClickable(true);
        }
    }
}
