package com.c.vpn.utill;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lenovo on 2018/7/19.
 */

public class ToastUtils {

    public static void showToastLong(Context context, String msg){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }

    public static void showToastShort(Context context, String msg){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }
}
