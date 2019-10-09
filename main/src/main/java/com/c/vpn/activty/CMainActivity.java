package com.c.vpn.activty;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.c.vpn.utill.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.activities.ConfigConverter;
import de.blinkt.openvpn.activities.DisconnectVPN;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import de.blinkt.openvpn.fragments.VPNProfileList;
import de.mrapp.android.dialog.MaterialDialog;

public class CMainActivity extends CBaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Toolbar toolbar;   //要导入v7包下的
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvTime;
    private AlertDialog alertDialog;

    private Button btnConnect;

    private boolean isConnect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmain);
        toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navigation_view_left);
        initDrawer();
        initToolbar();
        btnConnect = findViewById(R.id.btn_connect);
        findViewById(R.id.btn_connect).setOnClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        final MenuItem item = menu.findItem(R.id.action_edit);
        tvTime = (TextView) item.getActionView();
        tvTime.setText("剩余时间: 09D 10H 15M");
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                try {
                    //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        // Do something for lollipop and above versions
                        Window window = getWindow();
                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        // finally change the color to any color with transparency
                        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarktrans));}
                } catch (Exception e) {

                }
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions

                        Window window = getWindow();

                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                        // finally change the color again to dark
                        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    private void exitLogin(){
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this);
        dialogBuilder.setTitle("退出");
        dialogBuilder.setMessage("您确定要退出吗？");
        dialogBuilder.setPositiveButton(android.R.string.ok, null);
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
        MaterialDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawers();
        switch (menuItem.getItemId()){
            case R.id.item_serverlist:
                Intent intent = new Intent(CMainActivity.this,CServerListActivity.class);
                startActivity(intent);
                break;
            case R.id.item_account:
                Intent intent2 = new Intent(CMainActivity.this,CAccountActivity.class);
                startActivity(intent2);
                break;
            case R.id.item_shop:
                Intent intent3 = new Intent(CMainActivity.this,CShopActivity.class);
                startActivity(intent3);
                break;

            case R.id.item_about:
                Intent intent4 = new Intent(CMainActivity.this,CAboutActivity.class);
                startActivity(intent4);
                break;

            case R.id.item_setting:
                Intent intent5 = new Intent(CMainActivity.this,CSettingActivity.class);
                startActivity(intent5);
                break;
            case R.id.item_exit:
                exitLogin();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(!isConnect){
            startVpn();
        }else{
            if(VpnStatus.isVPNActive()){
                Intent disconnectVPN = new Intent(this, DisconnectVPN.class);
                startActivityForResult(disconnectVPN,4);
            }
        }
   /*     Collection<VpnProfile> allvpn = getPM().getProfiles();
        VpnProfile vpn = null;
        Iterator iterator = allvpn.iterator();
        while(iterator.hasNext()) {
             vpn = (VpnProfile) iterator.next();
        }
        Log.e("test","size:"+allvpn.size()+"uuid:"+vpn.getUUID().toString());
        Intent intent = new Intent(this, LaunchVPN.class);
        intent.putExtra(LaunchVPN.EXTRA_KEY, vpn.getUUID().toString());
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);*/

     /*   boolean isCopy = copyAssetAndWrite("demo.ovpn");
        File dataFile=new File(Environment.getExternalStorageDirectory(),"demo.ovpn");
        Uri uri = Uri.fromFile(dataFile);
        Uri uri2 = Uri.parse(dataFile.getAbsolutePath());
        Log.e("test",uri.getPath()+":"+uri2.getPath());
        startConfigImport(uri2);*/
    }

    private void startVpn(){
        String json = "client\n" +
                "nobind\n" +
                "dev tun\n" +
                "comp-lzo\n" +
                "tun-mtu 1500\n" +
                "resolv-retry infinite\n" +
                "remote-cert-tls server\n" +
                ";remote 156.236.75.96 16391 udp\n" +
                ";remote 156.233.65.151 16393 udp\n" +
                "remote 173.242.115.109 16393 udp\n" +
                "\n" +
                "cipher AES-256-CBC\n" +
                "key-direction 1\n" +
                "\n" +
                "<key>\n" +
                "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDDQRO3a06aXTKi\n" +
                "W1DUWSMoHXKqT6bBkqDmOQGocTBjDT1+RHlwBILLKKAozmnzlZYCS7aVdaM7wryW\n" +
                "pI5Asaji0mgDP3hxXeo1a6eKz/m9uZDL2bPztYlUJWmH72RN6VG8kCICgvdoKumV\n" +
                "yulhasPAD8tLcz2exqNpVLqqcEVc8Y/2IWGOZKM9XPrxcqpj9CSggZPyInJpvIC5\n" +
                "GPUe+Z/40ocCMVAa2gpiA5wVObNdGbKvoxO//kkR3xBsMZ3X/lnzgEDQaSSwyOzV\n" +
                "IQ10EF/ngBG8Np+hEUyCkg8K+8GuepsQ+pDPdvhB+x84L2Q6O+tF5F48TywcNqL1\n" +
                "hJxGYXr/AgMBAAECggEACps2XvhNRMVbQsaDFYXiCwPOfsHk5Q75/oeKN4QuhLLr\n" +
                "bDh8AEG+eRqz2IOd7cVqaKWEPYouW8iwRd6ae5kq+2dCjyqbeE0Js+0tJLU3c77o\n" +
                "xqoExHBQDWoa2W9/m1vZPMz3i/thRR9PoPtswgXyrDQvbuHLrxxLNCWOHkQLAIlm\n" +
                "ZQslkLAE1OuLwXwgIWqpSYRM1Tj2dyxGVzf99BxRJPsKBlDG8ZQnjJuo1IxzNZIh\n" +
                "QlK1fPSvX5bA00dPo+RcQAumqjXR/M5lpA/LT0ysNjdiD4mdWP3PGds5BUxEhHn8\n" +
                "GC2b9mGPh2Zt5SpkB1MUaKMgA3MaDIiVL4PzqBfSwQKBgQDkV1192fyCavxKbtua\n" +
                "6ONQeGau6XjINc++0iJyutnrHRBuRis+LqDqksWSc4+GlhdPG1EDC2ayR75CCWtW\n" +
                "4YletsO33cSmxikn2nmcxQ36eXdbSPT7SUpxk0TnFY1hqjulBTnUClNcyocDPA9m\n" +
                "WtESR4evL1QpcuXm5Z71/90NzQKBgQDa57cLstrJmWXRg7/KDGk6QZdGJZXXEKK5\n" +
                "mNg5CRDyUfcYD9Aw0YmVHKys/w5Ki/JdgaBRKTfH9NVqJA8gw4toOUq42ZU7kgDQ\n" +
                "4hn3XVIblguRtbmGPamCXZaVzMf9A8dC+l83l965T+fvbgKRBXM1WLb0wx7bkYoj\n" +
                "mwiDd72/+wKBgQDXm07iGMT/eHnE5qxht2l8gFOk8TtlALqq2cxBPQGrqYPsPEF0\n" +
                "s56qH/CCeq+ZS3uy7H4wO8G6sVeLD03VeZy2XCiuIq8fMpmF0htm1gJ+8dar0VXR\n" +
                "OMEcQQycjpMzFwS+qxKRjzZrtaiKe1mnMjWb5wLNfgkgIifHQCY67mASOQKBgQCT\n" +
                "77iSnWKij5pxxznb0BtlFWKoM0paN5HhH9wwX2ImgSIeDsHB3DnAncpMd2ovEvjO\n" +
                "HIY9kz9xUJLR0mCu4bfaza0j01e5FGicwdCOjXCp1/Mlm8wvXoip45mazvPwl5cY\n" +
                "OqASr/4C+m9iLdRqMWa0jKnIK/00odzbRzCvuEJx9QKBgA1UzzL8B+nLDm5DIFQp\n" +
                "FzyvDqkjf8AyH/KViFUOogSq/Hoqghe8Ax3m5A4OiVlXe2zDA0h8EuZ1y0o1p11T\n" +
                "vIIopev39hTXVT5iq5zwO/iYSpeaDv4mqItq7UM7XbVtc8BitlGCzUJM1EY6LbyD\n" +
                "AuSdRfljlQOEDat6Jfsha4Bl\n" +
                "-----END PRIVATE KEY-----\n" +
                "</key>\n" +
                "\n" +
                "<cert>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDWTCCAkGgAwIBAgIRAMlc+qmeh5CI4ssU9OnxkfAwDQYJKoZIhvcNAQELBQAw\n" +
                "FjEUMBIGA1UEAwwLRWFzeS1SU0EgQ0EwHhcNMTkwOTA5MDQ1NDM2WhcNMjIwODI0\n" +
                "MDQ1NDM2WjAVMRMwEQYDVQQDDApDTElFTlROQU1FMIIBIjANBgkqhkiG9w0BAQEF\n" +
                "AAOCAQ8AMIIBCgKCAQEAw0ETt2tOml0yoltQ1FkjKB1yqk+mwZKg5jkBqHEwYw09\n" +
                "fkR5cASCyyigKM5p85WWAku2lXWjO8K8lqSOQLGo4tJoAz94cV3qNWunis/5vbmQ\n" +
                "y9mz87WJVCVph+9kTelRvJAiAoL3aCrplcrpYWrDwA/LS3M9nsajaVS6qnBFXPGP\n" +
                "9iFhjmSjPVz68XKqY/QkoIGT8iJyabyAuRj1Hvmf+NKHAjFQGtoKYgOcFTmzXRmy\n" +
                "r6MTv/5JEd8QbDGd1/5Z84BA0GkksMjs1SENdBBf54ARvDafoRFMgpIPCvvBrnqb\n" +
                "EPqQz3b4QfsfOC9kOjvrReRePE8sHDai9YScRmF6/wIDAQABo4GiMIGfMAkGA1Ud\n" +
                "EwQCMAAwHQYDVR0OBBYEFC8DlbHA1zNzX+M5i1BSIvXa707WMFEGA1UdIwRKMEiA\n" +
                "FE/B6oMThrFrVYAzBdOrDZRwnldfoRqkGDAWMRQwEgYDVQQDDAtFYXN5LVJTQSBD\n" +
                "QYIUXcc47fSYjdzvnv6w9MblJo7rKq4wEwYDVR0lBAwwCgYIKwYBBQUHAwIwCwYD\n" +
                "VR0PBAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQDfCSU4G4fIDgRY9C1j5nIVI12Z\n" +
                "zdRuUeTeor57DVtECDwdHRsppkXCA46WGbnvBEqA2h2CEo0C2/bEaOogBJrCPS+a\n" +
                "AVwTZB2mIno/hbbU8y4L1zxDTe0iKu4p0skod2LbFkIrH1j4Nh4SvodGQettf+54\n" +
                "ww8CVO5LKpDPDtKruZr/GKBXEsJ4feWZdgDFXQzeq+IkuuSitBQ+yBhpklsZXNXh\n" +
                "kGAX09anXKyxyW5xkZWi47hOCMUDyp11LZb9xTk33IPrOTFGD1FwrcwwUjH0WUXM\n" +
                "Ks2NpYawoes5eryhP9Jc03ZQSJIHFHG1QgHN8SHfMoIvbO+a69Dm1Y0M05LG\n" +
                "-----END CERTIFICATE-----\n" +
                "</cert>\n" +
                "\n" +
                "<ca>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDSzCCAjOgAwIBAgIUXcc47fSYjdzvnv6w9MblJo7rKq4wDQYJKoZIhvcNAQEL\n" +
                "BQAwFjEUMBIGA1UEAwwLRWFzeS1SU0EgQ0EwHhcNMTkwOTA5MDQ1MzU0WhcNMjkw\n" +
                "OTA2MDQ1MzU0WjAWMRQwEgYDVQQDDAtFYXN5LVJTQSBDQTCCASIwDQYJKoZIhvcN\n" +
                "AQEBBQADggEPADCCAQoCggEBAOjsd4SBltE7xe7HfrOnCvgIDrZHiMsNeZFGA6lh\n" +
                "g5Bkkw4nFzRW874+9FaDevG2Urc5/pyUGrzpW9ftlxD43eiLMEfE5JzcNFg9ghly\n" +
                "qtyjBtkuvlimThQbin5EhhoBhLflpOGCZ7iiNt8P97RDru+b+94KY98dyw1Qgieo\n" +
                "G39bjK0vwZ7jHmpyYbM2fa+3y8UPdjqtnbrcGiUHCgQNQf5WzuwTzxyLUpvB8CsD\n" +
                "C0PHVPLV8lbVyPD/WjzHaAUpbyOFPHWhDMj5MmfWr0yqjzBKX4liLBQQN1NvgxrB\n" +
                "S6AS1IhOJ58kw1CsQVt6nNlh3vMm6KtI/0dz9T9WTAgktesCAwEAAaOBkDCBjTAd\n" +
                "BgNVHQ4EFgQUT8HqgxOGsWtVgDMF06sNlHCeV18wUQYDVR0jBEowSIAUT8HqgxOG\n" +
                "sWtVgDMF06sNlHCeV1+hGqQYMBYxFDASBgNVBAMMC0Vhc3ktUlNBIENBghRdxzjt\n" +
                "9JiN3O+e/rD0xuUmjusqrjAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjANBgkq\n" +
                "hkiG9w0BAQsFAAOCAQEANb7+j2aWHyPk6TGmblUPZEXl1e265GnxapEfP1bmJkbD\n" +
                "fhwhj824EOHb7I7tI5hVRiDWi4266MAAI780DHj/xlvaiG+LfSTag72aXIIBZOlQ\n" +
                "xcQSQsKQZlE39/YlImCkzUyd5L+OpwTwqbdVsbVq1p4e0tDH9k/iUrijFy5I3CpY\n" +
                "W+zqqj4nM0QOnvr83VQd7LODh8cbDu1dhkwqOP0UQVDqX7PWt0P42VuhT4JFkJD0\n" +
                "qofyEJ6ctdoDcy8JvQXc4QsXKSU2YOS33RjRoc9tdSviX8xWMmX7Is2/Rkaz24La\n" +
                "xxA+C05lnGp+HskZ96M2eBMjgqZ63VhP8v9tb22BNA==\n" +
                "-----END CERTIFICATE-----\n" +
                "</ca>\n" +
                "\n" +
                "<tls-auth>\n" +
                "-----BEGIN OpenVPN Static key V1-----\n" +
                "0226d6f5de094eeeb1bc1040926b5dfa\n" +
                "535803acf19b31a95207827a17eb845e\n" +
                "ee606fd2e63ba93ac508328edf05f94a\n" +
                "517201049b4a810c1bbb4789a36073f5\n" +
                "d38748316aa1f9cb4f1e08d9ae6c47f7\n" +
                "8df29a47092e07aa73495d6e5023f4ce\n" +
                "214ef6582236ad70ca46dce142832d23\n" +
                "5b09e11e8a61467e65c2d21d45186e0d\n" +
                "15f8398dbd5f1ea978c3a01d9d9855d6\n" +
                "d0c4150364a09c37ae2a3d04d50a2b51\n" +
                "7aee0596d5f705e284cf4034067bd92d\n" +
                "50fa6436938e16949f5e73b8fb26a845\n" +
                "9986b011d4f721dddccbcab3771c2b28\n" +
                "588b6482f94e963977604f4a139f2666\n" +
                "a18a9f9734ef87fbcb5ddbe66c7e338c\n" +
                "e74a25431f2ff987b9511ffa51100bbf\n" +
                "-----END OpenVPN Static key V1-----\n" +
                "</tls-auth>\n";
        File tmpProfile = createTmpProfile("test", json);
        startConfigImport(Uri.fromFile(tmpProfile));
    }

    private File createTmpProfile(String name, String config) {
        File tf = null;
        try {
            tf = new File(getExternalCacheDir(), name + ".ovpn");
            OutputStream os = new FileOutputStream(tf);
            os.write(config.getBytes(Charset.forName("utf-8")));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tf;
    }


    private ProfileManager getPM() {
        return ProfileManager.getInstance(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 231) {
            String profileUUID = data.getStringExtra(VpnProfile.EXTRA_PROFILEUUID);
            Intent intent = new Intent(this, LaunchVPN.class);
            intent.putExtra(LaunchVPN.EXTRA_KEY, profileUUID);
            intent.setAction(Intent.ACTION_MAIN);
            startActivityForResult(intent,3);
        }else if(requestCode == 3){
            btnConnect.setBackgroundColor(getResources().getColor(R.color.red));
            btnConnect.setText("STOP");
            isConnect = true;
        }else if(requestCode == 4 ){
            btnConnect.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnConnect.setText("CONNECT");
            isConnect = false;
        }
    }

    private void startConfigImport(Uri uri) {
        Intent startImport = new Intent(this, ConfigConverter.class);
        startImport.setAction(ConfigConverter.IMPORT_PROFILE);
        startImport.setData(uri);
        startActivityForResult(startImport, 231);
    }
}
