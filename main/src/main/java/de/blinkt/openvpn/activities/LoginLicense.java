package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
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

public class LoginLicense extends BaseActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login_license);

        findViewById(R.id.bt_checkLicense).setOnClickListener(view -> {
            if (checkLicense()) {
                Toast.makeText(LoginLicense.this, getString(R.string.check_license_ok),
                        Toast.LENGTH_SHORT).show();
                startMainActivity();
            } else {
                Toast.makeText(LoginLicense.this, getString(R.string.check_license_error),
                        Toast.LENGTH_LONG).show();
            }
        });

        if (checkLicenseLocal()) {
            startMainActivity();
        }

    }

    @Override
    protected void onResume() { super.onResume();}

    @Override
    protected void onPause() { super.onPause();}

    private boolean checkLicense() {

        String license = ((EditText) findViewById(R.id.editLicense)).getText().toString();
        String res;

        if (license.isEmpty())
            return false;

        // try to get response from url:<http>....?license=<number>
        PostUrl callable = new PostUrl(getString(R.string.license_url) + "?license=" + license);
        FutureTask<String> task = new FutureTask<>(callable);

        Thread t = new Thread(task);
        t.start();
        try {
            res = task.get();
            if (res == null)
                return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        // check response
        int leftTime = parserRespon(res);

        if (leftTime < 0)
            return false;

        // write to local disk
        restoreLicense(leftTime);
        return true;
    }

    private class PostUrl implements Callable<String> {
        private final String url;
        PostUrl(String url) {
            this.url = url;
        }
        public String call() throws Exception{
            URL url;
            try {
                url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                TrafficStats.setThreadStatsTag(12000);
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream res = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf, 0, 1024)) > 0) {
                        res.write(buf, 0, len);
                    }
                    inputStream.close();
                    String response = res.toString();
                    res.close();
                    return response;
                }
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
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
        startActivity(intent);
    }
}
