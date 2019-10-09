package com.c.vpn.activty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.c.vpn.common.Url;
import com.c.vpn.model.LoginEvent;
import com.c.vpn.model.RegCodeEvent;
import com.c.vpn.model.RegEvent;
import com.c.vpn.model.SaltEvent;
import com.c.vpn.utill.HttpUtil;
import com.c.vpn.utill.SaltUtil;
import com.c.vpn.utill.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.activities.BaseActivity;

public class CRegActivity extends CBaseActivity {
    private TextView tvCode;
    private EditText etUsername;
    private EditText etCode;
    private EditText etPassword;

    private Button btnReg;


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

        tvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                if(TextUtils.isEmpty(username)){
                    ToastUtils.showToastLong(CRegActivity.this,"邮箱不能为空");
                    return;
                }
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject result = HttpUtil.sendGet(Url.CODE+"?id="+username+"&method=0");
                        if(result != null){
                            EventBus.getDefault().post(new RegCodeEvent(result));
                        }else{
                            dissmissDialog();
                        }
                    }
                }).start();
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCode(RegCodeEvent event) {
        JSONObject obj = event.data;
        if(obj.getIntValue("code") != 0){
            ToastUtils.showToastLong(this,obj.getString("desc"));
        }else{
            tvCode.setText("已发送至您的邮箱");
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
}
