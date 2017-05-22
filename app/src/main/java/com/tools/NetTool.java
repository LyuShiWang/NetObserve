package com.tools;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 获得当前网络信息,已经扫描同网段的ip
 * Created by win10 on 2017/5/12.
 */

public class NetTool {
    private int SERVERPORT = 8888;
    private String locAddress;//存储本机ip，例：本地ip ：192.168.1.
    private String locAddrIndex;//存储IP前缀
    private String current_ip;
    private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
    private Process proc = null;
    private String ping = "ping -c 1 -w 0.5 ";//其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
    private int j;//存放ip最后一位地址 0-255
    private ArrayList<String> list_IPs = new ArrayList<String>();
    private Context ctx;//上下文

    public NetTool(Context ctx) {
        this.ctx = ctx;
    }

    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 222:// 服务器消息
                    break;
                case 333:// 扫描完毕消息
                    Toast.makeText(ctx, "扫描到主机：" + ((String) msg.obj).substring(6), Toast.LENGTH_LONG).show();
                    break;
                case 444://扫描失败
                    Toast.makeText(ctx, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case 555://获得一个可连接的IP地址
                    if (!current_ip.equals("") || !current_ip.equals(null)) {
                        list_IPs.add(current_ip);
                    }
            }
        }
    };


    //向serversocket发送消息
    public String sendMsg(String ip, String msg) {
        String res = null;
        Socket socket = null;
        try {
            socket = new Socket(ip, SERVERPORT);
            //向服务器发送消息
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            os.println(msg);
            os.flush();// 刷新输出流，使Server马上收到该字符串

            //从服务器获取返回消息
            DataInputStream input = new DataInputStream(socket.getInputStream());
            res = input.readUTF();
            System.out.println("server 返回信息：" + res);
            Message.obtain(handler, 222, res).sendToTarget();//发送服务器返回消息

        } catch (Exception unknownHost) {
            System.out.println("You are trying to connect to an unknown host!");
        } finally {
            // 4: Closing connection
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return res;
    }


    /**
     * 扫描局域网内ip，找到对应服务器
     */
    public void scan() {
        locAddrIndex = getLocAddrIndex();//获取本地ip前缀
        if (locAddrIndex.equals("")) {
            Toast.makeText(ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < 256; i++) {//创建256个线程分别去ping
            j = i;
            new Thread(new Runnable() {
                public void run() {
                    String p = NetTool.this.ping + locAddrIndex + NetTool.this.j;
                    current_ip = locAddrIndex + NetTool.this.j;
                    System.out.println("正在尝试连接" + locAddrIndex + j);
                    if (!current_ip.equals(locAddress)) {
                        try {
                            proc = run.exec(p);
                            int result = proc.waitFor();
                            if (result == 0) {
                                System.out.println("连接成功！IP地址为：" + current_ip);
                                list_IPs.add(current_ip);
//                                // 向服务器发送验证信息
//                                String msg = sendMsg(current_ip, "scan" + getLocAddress() + " ( "
//                                        + android.os.Build.MODEL + " ) ");
//
//                                //如果验证通过...
//                                if (msg != null) {
//                                    if (msg.contains("OK")) {
//                                        System.out.println("服务器IP：" + msg.substring(8, msg.length()));
//                                        Message.obtain(handler, 333, msg.substring(2, msg.length())).sendToTarget();
//                                        //返回扫描完毕消息
//                                    }
//                                }
                            } else {
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        } finally {
                            proc.destroy();
                        }
                    }
                }
            }).start();
        }
    }

    public ArrayList<String> getList_IPs() {
        return list_IPs;
    }

    //获取本地ip地址
    public String getLocAddress() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }
//        System.out.println("本机IP:" + ipaddress);
        return ipaddress;

    }

    //获取IP前缀
    public String getLocAddrIndex() {
        String str = getLocAddress();
        if (!str.equals("")) {
            return str.substring(0, str.lastIndexOf(".") + 1);
        }
        return "";
    }

    //获取本机设备名称
    public String getLocDeviceName() {
        return android.os.Build.MODEL;
    }
}