package com.tools;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获得当前网络信息,已经扫描同网段的ip
 * Created by win10 on 2017/5/17.
 */

public class NetUtil {
    private Context context;

    private String localIpAddress;//本地ip地址 如 192.168.0.1
    private String networkSegment;//本地网段 如 192.168.0.
    private int ipIndex;//ip最后位 如 1
    private volatile List<String> ipListInSameSegment = new ArrayList<>();//存放同网段的ip
    private boolean isRoot=false;//是否root

    private String ping = "ping -c 3 -w 10 ";//-c 是指ping的次数 -w 100  以秒为单位指定超时间

    private Runtime runtime = Runtime.getRuntime();
    private Process process = null;

    //定义WifiManager对象
    private WifiManager mWifiManager;
    //定义WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    //定义一个WifiLock
    WifiManager.WifiLock mWifiLock;


    //判断是否root
    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    public NetUtil(Context context){
        this.context = context;

        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();


        mWifiManager.startScan();
        //得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();

        //设置本地ip
        String ip = getLocalIpAddress().toString();
        localIpAddress = ip.substring(1, ip.length());
        networkSegment = this.localIpAddress.substring(0, this.localIpAddress.lastIndexOf(".") + 1);
    }


    /**
     * 获取本地ip
     */
    public InetAddress getLocalIpAddress() {
        int hostAddress = mWifiInfo.getIpAddress();
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    /**
     * 扫描同网段的ip
     */
    public List<String> scanIpInSameSegment(){
        if(this.localIpAddress == null || "".equals(this.localIpAddress)){
//            Toast.makeText(context, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
            return null;
        }

        if(!NetUtil.isRootSystem()){
            Toast.makeText(context, "扫描网络ip需要root权限,请先root后再尝试!", Toast.LENGTH_LONG).show();
            return null;
        }

        //产生256个线程测试ip
        for(int i=0;i<256;i++){
            ipIndex = i;
            new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    String currentIp = networkSegment + ipIndex;
                    String command = ping + currentIp;
                    try {
                        process = runtime.exec(command);
                        int result = process.waitFor();
                        if (result == 0) {
                            System.out.println("连接成功:" + currentIp);
                            Log.i("IP", "连接成功:" + currentIp);
                            ipListInSameSegment.add(currentIp);
                        } else {
                            System.out.println("连接失败:" + currentIp);
                            Log.i("IP", "连接失败:" + currentIp);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    } finally {
                        process.destroy();
                    }
                }
            }).start();
        }
        return ipListInSameSegment;
    }

    /**
     * 获得root权限
     */
    public void getRootPermission(){

        if(NetUtil.isRootSystem()){
            return;
        }

        try {
            String rootCommand = "su";
            process = runtime.exec(rootCommand);

            int result = process.waitFor();

            if (result == 0) {
                this.isRoot = true;
                Log.i("IP", "Root成功");
                Toast.makeText(context, "Root成功", Toast.LENGTH_LONG).show();
            } else {
                Log.i("IP", "Root失败");
                Toast.makeText(context, "Root失败", Toast.LENGTH_LONG).show();
            }

//            //输出结果
//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                Log.i("IP", ">>>:" + line);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断是否已经root
     */
    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/" };
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }
}
