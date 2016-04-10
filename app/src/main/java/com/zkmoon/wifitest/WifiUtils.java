package com.zkmoon.wifitest;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description Wi-Fi管理工具类
 * @Author AA
 * @DateTime 16/4/5 下午2:59
 */
public class WifiUtils {

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private WifiManager.WifiLock mWifiLock;

    /**
     * 扫描到的Wi-Fi列表
     */
    private List<ScanResult> mScanResults;
    /**
     * 已保存的Wi-Fi网络列表
     */
    private List<WifiConfiguration> mWifiConfigurations;

    public static final int NONE = 0;
    public static final int WPA2_PSK = 4;


    public WifiUtils(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 判空处理
     *
     * @param str
     * @return
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    /**
     * 检验当前SSID是否已存在,以免出现重复SSID
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig != null && existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 扫描当前可用Wi-Fi
     */
    public void startScan() {
        mWifiManager.startScan();
        //获得扫描结果
        mScanResults = mWifiManager.getScanResults();
        //获得已保存的Wi-Fi网络列表
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    /**
     * 创建一个Wi-Fi锁
     *
     * @param tag
     */
    public void createWifiLock(String tag) {
        mWifiLock = mWifiManager.createWifiLock(tag);
    }

    /**
     * 锁定当前Wi-Fi
     */
    public void acquireWifiLock() {
        if (mWifiLock != null) {
            mWifiLock.acquire();
        }
    }

    /**
     * 解锁当前Wi-Fi
     */
    public void releaseWifiLock() {
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    /**
     * 获得接入点的BSSID
     *
     * @return
     */
    public String getBSSID() {
        return mWifiInfo == null ? null : mWifiInfo.getBSSID();
    }

    /**
     * 获得接入点的SSID
     *
     * @return
     */
    public String getSSID() {
        return mWifiInfo == null ? null : mWifiInfo.getSSID();
    }

    /**
     * 根据SSID查networkID
     *
     * @param SSID
     * @return
     */
    public int getNetworkIdBySSID(String SSID) {
        if (isEmpty(SSID)) {
            return 0;
        }
        WifiConfiguration config = isExsits(SSID);
        if (config != null) {
            return config.networkId;
        }
        return 0;
    }

    /**
     * 获得IP地址
     *
     * @return
     */
    public int getIPAddress() {
        return mWifiInfo == null ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 获得MAC地址
     *
     * @return
     */
    public String getMacAddress() {
        return mWifiInfo == null ? null : mWifiInfo.getMacAddress();
    }

    /**
     * 获得接入点的ID
     *
     * @return
     */
    public int getNetworkId() {
        return mWifiInfo == null ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 返回扫描结果
     *
     * @return
     */
    public List<ScanResult> getScanResults() {
        return mScanResults;
    }

    /**
     * 返回已保存的Wi-Fi网络列表
     *
     * @return
     */
    public List<WifiConfiguration> getWifiConfigurations() {
        return mWifiConfigurations;
    }

    /**
     * 添加一个网络并连接
     *
     * @param wcg
     * @return
     */
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        return mWifiManager.enableNetwork(wcgID, true);
    }

    /**
     * 设置Wi-Fi热点配置信息
     *
     * @param SSID
     * @param pwd
     * @param keyMgmt
     * @return
     */
    public WifiConfiguration setWifiConfig(String SSID, String pwd, int keyMgmt) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = SSID;

        switch (keyMgmt) {
            case NONE:
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case WPA2_PSK:
                wifiConfiguration.preSharedKey = pwd;
                //WPA2_PSK = 4,在KeyMgmt中被隐藏,设置4即可开启WPA2热点
                wifiConfiguration.allowedKeyManagement.set(WPA2_PSK);
                break;
        }
        return wifiConfiguration;
    }

    /**
     * 打开Wi-Fi
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wi-Fi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 开启Wi-Fi热点
     *
     * @param wifiConfiguration Wi-Fi配置信息
     */
    public boolean openWifiAp(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return false;
        }
        Method method = null;
        try {
            closeWifi();
            //使用反射开启Wi-Fi热点
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", wifiConfiguration.getClass(), boolean.class);
            method.invoke(mWifiManager, wifiConfiguration, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭Wi-Fi热点
     *
     * @param wifiConfiguration Wi-Fi配置信息
     * @return
     */
    public boolean closeWifiAp(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return false;
        }
        Method method = null;
        try {
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", wifiConfiguration.getClass(), boolean.class);
            method.invoke(mWifiManager, wifiConfiguration, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 连接一个Wi-Fi热点
     *
     * @param SSID
     * @param pwd
     * @return
     */
    public boolean connectWifi(String SSID, String pwd) {
        WifiConfiguration config = null;
        if (isEmpty(SSID)) {
            return false;
        }

        openWifi();
        if (isEmpty(pwd)) {
            config = setWifiConfig(SSID, pwd, NONE);
        } else {
            config = setWifiConfig(SSID, pwd, WPA2_PSK);
        }
        return addNetwork(config);
    }

    /**
     * 断开指定ID的网络
     *
     * @param SSID
     */
    public void disconnectWifi(String SSID) {
        mWifiManager.disableNetwork(getNetworkIdBySSID(SSID));
        mWifiManager.disconnect();
    }

}
