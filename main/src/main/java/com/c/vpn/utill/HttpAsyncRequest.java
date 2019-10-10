package com.c.vpn.utill;

import android.net.TrafficStats;
import android.os.AsyncTask;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAsyncRequest extends AsyncTask<String, Void, Void> {
    private int timeout_request = 3000;
    private int timeout_response = 3000;
    private HttpAsyncCallback callback;

    public interface HttpAsyncCallback {
        void completionHandler(Boolean success, JSONObject obj);
    }

    public HttpAsyncRequest(int requestTimeout, int readTimeout, HttpAsyncCallback callback) {
        this.timeout_request = requestTimeout;
        this.timeout_response = readTimeout;
        this.callback = callback;
    }

    public HttpAsyncRequest(HttpAsyncCallback callback) {
        this.callback = callback;
    }

    public Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);

            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(timeout_request);
            urlConnection.setReadTimeout(timeout_response);
            TrafficStats.setThreadStatsTag(12000);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                Log.e("HttpGET", buffer.toString());
                String data = buffer.toString();
                JSONObject obj = JSON.parseObject(data);

                if (callback != null) {
                    callback.completionHandler(true, obj);
                }
            } else {
                    callback.completionHandler(true, null);
            }
        } catch (Exception e) {
            Log.e("Http Async request", e.getMessage());
            e.printStackTrace();
            callback.completionHandler(true, null);
        }
        return null;
    }
};
