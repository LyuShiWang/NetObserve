package com.lyushiwang.netobserve;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tools.My_Func;
import com.tools.NetTool;
import com.tools.NetUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/5/12.
 */

public class UploadData extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private My_Func my_functions = new My_Func();
    private NetTool netTool = new NetTool(UploadData.this);

    private ListView listView_project;
    private List<String> list_project = new ArrayList<String>();
    private ArrayAdapter<String> project_adapter;
    private String ProjectName;
    private String localAddress;//存储本机ip
    private String localDeviceName;//存储本机设备名
    private Handler MsgHandler;

    private File file_in2 = new File(my_functions.get_main_file_path() + "/" + ProjectName, ProjectName + ".in2");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_data);

        init();

        //        localAddress =netTool.getLocAddress();
        //        String Index=netTool.getLocAddrIndex();
        //        String Name=netTool.getLocDeviceName();
        //        netTool.scan();
        //
        //        String text="本机IP地址为："+ localAddress +"\n"+
        //                "本机IP地址前缀为："+Index+"\n"+
        //                "本机设备名为："+Name;
        AlertDialog.Builder AD_IPAddress = new AlertDialog.Builder(UploadData.this);
        //        AD_IPAddress.setMessage(text).setPositiveButton("确定",null).create().show();

        MsgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    makeToast("平差完毕！请接收.ou2文件！");
                    AlertDialog.Builder AD_check = new AlertDialog.Builder(UploadData.this);
                    AD_check.setMessage("平差完毕！请接收.ou2文件！").show();
                }
                if (msg.what == 2) {
                    makeToast("平差出错！");
                }
            }
        };
    }

    protected void init() {
        listView_project = (ListView) findViewById(R.id.listview_project);

        File ProjectList = my_functions.get_ProjectList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(ProjectList));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                list_project.add(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        project_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, list_project);
        listView_project.setAdapter(project_adapter);
        listView_project.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder AD_upload = new AlertDialog.Builder(UploadData.this);
        AD_upload.setTitle("确认框").setMessage("是否确定上传该工程数据？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProjectName = project_adapter.getItem(position);
                        uploadproject(ProjectName);
                        //                        makeToast("已上传！");
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    public void uploadproject(String ProjectName) {
        file_in2 = new File(my_functions.get_main_file_path() + "/" + ProjectName, ProjectName + ".in2");
        if (file_in2.exists()) {
            //将该in2文件上传即可
            //connect_PC(ProjectName);

            Thread connect = new Thread(new connectPC());
            connect.start();

            Thread check_thread = new Thread(new CheckThread());
            check_thread.start();
        } else {
            makeToast("没有找到in2文件！");
        }
    }

    //        public static String getLocalIpAddress(){
    //        try{
    //            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    //                NetworkInterface intf = en.nextElement();
    //                for (Enumeration<InetAddress> enumIpAddr = intf
    //                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
    //                    InetAddress inetAddress = enumIpAddr.nextElement();
    //                    if (!inetAddress.isLoopbackAddress()
    //                            && inetAddress instanceof Inet4Address) {
    //                        return inetAddress.getHostAddress().toString();
    //                    }
    //                }
    //            }
    //        }catch (SocketException e) {
    //            Log.i("", "WifiPreference IpAddress---error-" + e.toString());
    //        }
    //        return null;
    //    }
    public void connect_PC(final String ProjectName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    //1.创建客户端Socket，指定服务器地址和端口
                    String ServeIPAdress = "10.6.0.54";
                    Socket socket = new Socket(ServeIPAdress, 8884);
                    //2.获取输出流，向服务器端发送信息
                    OutputStream os = socket.getOutputStream();//字节输出流
                    PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
                    pw = new PrintWriter(socket.getOutputStream());
                    //获取客户端的IP地址
                    //                    InetAddress address = InetAddress.getLocalHost();
                    //                    String ip = address.getHostAddress();
                    pw.write(ProjectName + "\n");
                    try {
                        String line;
                        BufferedReader bf = new BufferedReader(new FileReader(file_in2));
                        while (((line = bf.readLine()) != null)) {
                            pw.write(line + "\n");
                        }
                    } catch (Exception e) {
                    }
                    pw.flush();//刷新打印流，这样就把pw中的数据发送到服务器端
                    pw.close();//关闭打印流
                    socket.shutdownOutput();//关闭输出流
                    socket.close();//关闭套接字
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class connectPC implements Runnable {
        @Override
        public void run() {
            try {
                //1.创建客户端Socket，指定服务器地址和端口
                String ServeIPAdress = "10.6.0.54";
                Socket socket = new Socket(ServeIPAdress, 8884);
                //2.获取输出流，向服务器端发送信息
                OutputStream os = socket.getOutputStream();//字节输出流
                PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
                pw = new PrintWriter(socket.getOutputStream());
                //获取客户端的IP地址
                //                    InetAddress address = InetAddress.getLocalHost();
                //                    String ip = address.getHostAddress();
                pw.write(ProjectName + "\n");
                try {
                    String line;
                    BufferedReader bf = new BufferedReader(new FileReader(file_in2));
                    while (((line = bf.readLine()) != null)) {
                        pw.write(line + "\n");
                    }
                } catch (Exception e) {
                }
                pw.flush();//刷新打印流，这样就把pw中的数据发送到服务器端

                Thread check_thread=new Thread(new CheckThread());
                check_thread.start();
//
//                Thread testthread=new Thread(new TestThread());
//                testthread.start();

                pw.close();//关闭打印流
                socket.close();//关闭套接字

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class CheckThread implements Runnable {
        public void run() {
            int i=0;
            label1:while(true) {
                try {
                    Log.i("Worning:", "CheckThread线程正在运行 "+String.valueOf(i));
                    i=i+1;
                    Thread.sleep(1000);

                    //等待服务器传回“平差完毕”的讯号
                    String ServeIPAdress = "10.6.0.54";
                    Socket socket_check = new Socket(ServeIPAdress, 12345);
                    BufferedReader br_check = new BufferedReader(new InputStreamReader(socket_check.getInputStream()));

                    String ischecked = "";

                    while ((ischecked = br_check.readLine()) != null) {
                        Log.i("Tips:", "已连接，已接收到反馈信息");
                        Message msg = new Message();
                        if (ischecked.equals("checked")) {
                            msg.what = 1;
                            MsgHandler.sendMessage(msg);
                            break label1;
                        } else {
                            msg.what = 2;
                            MsgHandler.sendMessage(msg);
                            break label1;
                        }
                    }
                } catch (Exception e) {
                }

                if (i>10){
                    break label1;
                }
            }
        }
    }

    class TestThread implements Runnable{
        @Override
        public void run() {
            try{
                int i=0;
                while(true){
                    Log.i("Test","Testing"+String.valueOf(i));
                    Thread.sleep(1000);
                    i=i+1;

                    if(i>1000){
                        break;
                    }
                }
            }catch (Exception e){

            }
        }
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
