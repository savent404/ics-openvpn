package com.c.vpn.activty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.c.vpn.common.Url;
import com.c.vpn.utill.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import de.blinkt.openvpn.R;

public class CLoginActivity extends CBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clogin);
        initActionBar();
        initViews();
    }

    private void initViews() {
        findViewById(R.id.tv_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CLoginActivity.this,CForgetPassWordActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(this);
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
                startActivity(intent);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void goToMain(){
        Intent intent = new Intent(CLoginActivity.this,CMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        //goToMain();
        OkGo.<String>get(Url.SALT).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String result = response.body();
                Log.e("test",result);
                ToastUtils.showToastLong(CLoginActivity.this,result);
            }
        });
    }

}
