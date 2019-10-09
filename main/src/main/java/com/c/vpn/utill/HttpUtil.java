package com.c.vpn.utill;

import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {


    /**
     * Get服务请求
     *
     * @param requestUrl
     * @return
     */
    public static JSONObject sendGet(String requestUrl){
        Log.e("HttpGET","url:"+requestUrl);
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            TrafficStats.setThreadStatsTag(12000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            Log.e("HttpGET", "responseCode:"+responseCode);

            if (HttpURLConnection.HTTP_OK == responseCode) { //连接成功
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                Log.e("HttpGET", buffer.toString());
                String data = buffer.toString();
                JSONObject obj = JSON.parseObject(data);
                return obj;
            }
        }catch (Exception e){
            Log.e("HttpGET", "error:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
