package com.zkmoon.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import org.greenrobot.eventbus.EventBus;

/**
 * @Description Wi-Fi广播接收器
 * @Author AA
 * @DateTime 16/4/5 下午4:29
 */
public class WifiReceiver extends BroadcastReceiver {

    private static final String TYPE_WIFI = "WIFI";
    private static final String EXTRA_INFO_UNKNOW = "<unknown ssid>";


    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.isConnectedOrConnecting()
                && networkInfo.getTypeName().equals(TYPE_WIFI)
                && !networkInfo.getExtraInfo().equals(EXTRA_INFO_UNKNOW)) {
            //连接成功
            EventBus.getDefault().post(networkInfo);
        }


    }

}
