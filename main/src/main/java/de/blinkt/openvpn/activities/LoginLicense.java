package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.fragments.VPNProfileList;

public class LoginLicense extends BaseActivity {

    private String editLicense;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login_license);

        findViewById(R.id.bt_checkLicense).setOnClickListener(view -> {
            checkLicense();
        });

        if (checkLicenseLocal()) {
            startMainActivity();
        }

    }

    @Override
    protected void onResume() { super.onResume();}

    @Override
    protected void onPause() { super.onPause();}

    private static class MessageHandler extends Handler {
        private final WeakReference<LoginLicense> mActivity;
        public MessageHandler(LoginLicense activity) { mActivity = new WeakReference<>(activity); }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            LoginLicense activity = mActivity.get();

            if (activity == null)
                return;

            Bundle bundle = msg.getData();
            String json = bundle.getString("json");
            int leftTime;
            if (json == null || (leftTime = activity.parserRespon(json)) < 0) {
                Toast.makeText(activity, activity.getString(R.string.check_license_error),
                        Toast.LENGTH_LONG).show();
                return;
            }
            activity.restoreLicense(leftTime);
            Toast.makeText(activity, activity.getString(R.string.check_license_ok),
                    Toast.LENGTH_LONG).show();
            activity.startMainActivity();
        }
    }
    private Handler messageHandler = new LoginLicense.MessageHandler(this);
    private void checkLicense() {

        this.editLicense = ((EditText) findViewById(R.id.editLicense)).getText().toString();

        if (checkLicenseLocal())
            return;
        if (this.editLicense.isEmpty()) {
            messageHandler.sendEmptyMessage(0);
            return;
        }
        new Thread(PostUrl).start();
    }

    private Runnable PostUrl = () -> {
        try {
            URL url = new URL(getString(R.string.license_url) + "?license=" + this.editLicense);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            TrafficStats.setThreadStatsTag(12000);
            int code = connection.getResponseCode();

            if (code == 200) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream res = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf, 0, 1024)) > 0) {
                    res.write(buf, 0, len);
                }
                inputStream.close();
                String response = res.toString();
                Bundle bundle = new Bundle();
                Message msg = new Message();
                bundle.putString("json", response);
                msg.setData(bundle);
                messageHandler.sendMessage(msg);
            } else {
                messageHandler.sendEmptyMessage(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageHandler.sendEmptyMessage(0);
        }
    };
    private int parserRespon(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (!obj.getString("status").equals("ok"))
                return -1;
            return Integer.parseInt(obj.getString("res"));
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void restoreLicense(int time) {
        Date dateNow = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        calendar.add(Calendar.HOUR, time * 24);

        try {
            Context context = getApplicationContext();
            FileOutputStream fos = context.openFileOutput("config", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(calendar.getTime());
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("TIME", calendar.getTime().toString());
    }
    private boolean checkLicenseLocal() {
        try {
            Context context = getApplicationContext();
            FileInputStream fis = context.openFileInput("config");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Date date = (Date) ois.readObject();
            ois.close();
            Date currentDate = new Date();

            if (date.compareTo(currentDate) >= 0)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    private void startMainActivity() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(LoginLicense.this, MainActivity.class);
        startActivity(intent);
    }
}
