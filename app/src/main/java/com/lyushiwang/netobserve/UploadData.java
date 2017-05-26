package com.lyushiwang.netobserve;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tools.My_Functions;
import com.tools.NetTool;
import com.tools.NetUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/5/12.
 */

public class UploadData extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private My_Functions my_functions = new My_Functions();
    private NetTool netTool = new NetTool(UploadData.this);

    private ListView listView_project;
    private List<String> list_project = new ArrayList<String>();
    private ArrayAdapter<String> project_adapter;
    private String ProjectName;

    private String localAddress;//存储本机ip
    private String locAddrIndex;
    private String localDeviceName;//存储本机设备名
    private int j;
    private int IP_connect;

    private File file_in2 = new File(my_functions.get_main_file_path() + "/" + ProjectName, ProjectName + ".in2");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_data);

        init();

        localAddress = netTool.getLocAddress();//获取本机IP地址
        locAddrIndex = netTool.getLocAddrIndex();
//        netTool.scan();//搜索同一局域网的IP地址
//        list_IPs = netTool.getList_IPs();//获取IP地址

//        String text1 = "本机IP为：" + localAddress;
//        AlertDialog.Builder AD_IPAddress = new AlertDialog.Builder(UploadData.this);
//        AD_IPAddress.setMessage(text1).setPositiveButton("确定", null).create().show();
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
                        makeToast("上传成功");
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    public void uploadproject(String ProjectName) {
        file_in2 = new File(my_functions.get_main_file_path() + "/" + ProjectName, ProjectName + ".in2");
        if (file_in2.exists()) {
            //将该in2文件上传即可
            connect_PC();
        } else {
            makeToast("没有找到in2文件！");
        }
    }

    public void connect_PC() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //1.创建客户端Socket，指定服务器地址和端口
                    String serviceIP = "192.168.6.28";
                    Socket socket = new Socket(serviceIP, 12345);
                    //2.获取输出流，向服务器端发送信息
                    OutputStream os = socket.getOutputStream();//字节输出流
                    OutputStreamWriter osw=new OutputStreamWriter(os, "UTF-8");
                    BufferedWriter bw=new BufferedWriter(osw);
                    PrintWriter pw = new PrintWriter(bw,true);//将输出流包装为打印流
//                    //获取客户端的IP地址
//                    InetAddress address = InetAddress.getLocalHost();
//                    String ip = address.getHostAddress();
//                        pw.write("：" + "客户端：" + ip + "接入服务器" + "\n");
//                        pw.write("：" + "等待上传.in2文件");
                    try {
                        String line;
                        BufferedReader bf = new BufferedReader(new FileReader(file_in2));
                        pw.write(ProjectName+ "\n");
                        while (((line = bf.readLine()) != null)) {
                            pw.write(line + "\n");
                        }
                    } catch (Exception e) {
                    }
                    pw.flush();
                    socket.shutdownOutput();//关闭输出流
                    socket.close();
                    System.out.println("上传成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void connect_PC2() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 1; i < 256; i++) {//创建256个线程分别去ping
                    j = i;
                    try {
                        //1.创建客户端Socket，指定服务器地址和端口
//                        String serviceIP = "192.168.6.28";
                        String serviceIP = locAddrIndex + j;
                        Socket socket = new Socket(serviceIP, 12345);
                        System.out.println("第" + j + "次：" + "连接" + locAddrIndex + j + "成功！");
                        IP_connect = j;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("第" + j + "次：" + locAddrIndex + j + "未连接成功！");
                    }
                }
            }
        }.start();

        makeToast(locAddrIndex + IP_connect);

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    //1.创建客户端Socket，指定服务器地址和端口
////                    String serviceIP = "192.168.6.28";
//                    String serviceIP = locAddrIndex + IP_connect;
//                    Socket socket = new Socket(serviceIP, 12345);
//                    //2.获取输出流，向服务器端发送信息
//                    OutputStream os = socket.getOutputStream();//字节输出流
//                    PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
//                    //获取客户端的IP地址
//                    InetAddress address = InetAddress.getLocalHost();
//                    String ip = address.getHostAddress();
////                        pw.write("：" + "客户端：" + ip + "接入服务器" + "\n");
////                        pw.write("：" + "等待上传.in2文件");
//                    pw.write(ProjectName);
//                    try {
//                        String line;
//                        BufferedReader bf = new BufferedReader(new FileReader(file_in2));
//                        while (((line = bf.readLine()) != null)) {
//                            pw.write(line + "\n");
//                        }
//                    } catch (Exception e) {
//                    }
//                    System.out.println("第" + j + "次：" + "连接" + locAddrIndex + j + "成功！");
//                    pw.flush();
//                    socket.shutdownOutput();//关闭输出流
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("第" + j + "次：" + locAddrIndex + j + "未连接成功！");
//                }
//            }
//        }.start();
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
