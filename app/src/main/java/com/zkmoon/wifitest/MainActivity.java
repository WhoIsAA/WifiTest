package com.zkmoon.wifitest;

import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private WifiUtils mWifiUtils;
    private WifiReceiver mWifiReceiver;
    private boolean mIsConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mWifiUtils = new WifiUtils(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver();
    }

    //创建Wi-Fi热点
    @OnClick(R.id.btn_open_wifiAp)
    public void openWifiAp() {
        WifiConfiguration configuration = mWifiUtils.setWifiConfig("WifiTest", "WifiTest123", WifiUtils.WPA2_PSK);
        if (mWifiUtils.openWifiAp(configuration)) {
            Toast.makeText(this, "创建成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "创建失败", Toast.LENGTH_SHORT).show();
        }
    }

    //关闭Wi-Fi热点
    @OnClick(R.id.btn_close_wifiAp)
    public void closeWifiAp() {
        WifiConfiguration configuration = mWifiUtils.setWifiConfig("WifiTest", "WifiTest123", WifiUtils.WPA2_PSK);
        mWifiUtils.closeWifiAp(configuration);
    }

    //连接指定Wi-Fi
    @OnClick(R.id.btn_connect_wifi)
    public void connectWifi() {
        registerReceiver();
        mWifiUtils.connectWifi("MyWifi", "123456789");
    }
    
    //断开指定Wi-Fi
    @OnClick(R.id.btn_disconnect_wifi)
    public void disconnectWifi() {
        mWifiUtils.disconnectWifi("MyWifi");
    }

    @Subscribe
    public void onEventMainThread(NetworkInfo networkInfo) {
        if (networkInfo != null && !mIsConnected) {
            mIsConnected = true;
            unregisterReceiver();
            Toast.makeText(this, "成功连接到" + networkInfo.getExtraInfo(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "成功连接到 " + String.valueOf(networkInfo));
        }
    }

    private void registerReceiver() {
        if (mWifiReceiver == null) {
            mWifiReceiver = new WifiReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mWifiReceiver);
    }

}
