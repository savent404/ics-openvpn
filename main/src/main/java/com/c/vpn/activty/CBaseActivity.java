package com.c.vpn.activty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import de.blinkt.openvpn.R;
import de.mrapp.android.dialog.ProgressDialog;

public class CBaseActivity extends AppCompatActivity {

    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbase);
        ProgressDialog.Builder dialogBuilder = new ProgressDialog.Builder(this);
        dialogBuilder.setTitle("加载中...");
        dialogBuilder.setProgressBarPosition(ProgressDialog.ProgressBarPosition.LEFT);
        dialog = dialogBuilder.create();
    }

    public void showDialog(){
        dialog.show();
    }

    public void dissmissDialog(){
        dialog.dismiss();
    }



    public void initActionBar(){

    }



}
