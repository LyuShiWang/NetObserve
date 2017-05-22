package com.lyushiwang.netobserve;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tools.My_Functions;
import com.tools.NetTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/5/13.
 */

public class Feedback extends AppCompatActivity  {

    private Button button_get_result;

    private My_Functions my_functions = new My_Functions();
    private NetTool netTool = new NetTool(Feedback.this);

    private ListView listView_project;
    private List<String> list_project = new ArrayList<String>();
    private ArrayAdapter<String> project_adapter;
    private String ProjectName;

    private Handler MsgHandler;//消息处理

    private String localAddress;//存储本机ip
    private String locAddrIndex;//存储IP前缀
    private String localDeviceName;//存储本机设备名
    private int j;
    private int IP_connect;
    private ArrayList<String> list_IPs = new ArrayList<String>();

    private File file_in2 = new File(my_functions.get_main_file_path() + "/" + ProjectName, ProjectName + ".in2");


    @Override
    //主程序
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_result);

        define_palettes();

        Intent intent1_5_6 = getIntent();
        String ProjectName_now = intent1_5_6.getStringExtra("ProjectName_now");//读取当前工程名
        makeToast("当前工程为："+ProjectName_now);

        localAddress = netTool.getLocAddress();//获取本机IP地址
        locAddrIndex = netTool.getLocAddrIndex();
    }

    public void define_palettes(){
        button_get_result=(Button)findViewById(R.id.button_get_result);
    }

    public void get_result(View v){
        new Thread(new Runnable() {
            public void run() {
                try {
                    //1.创建客户端Socket，指定服务器地址和端口
                    String serviceIP = "192.168.6.28";
                    Socket socket = new Socket(serviceIP, 12345);


                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
