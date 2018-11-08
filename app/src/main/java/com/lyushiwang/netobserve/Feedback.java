package com.lyushiwang.netobserve;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tools.My_Func;
import com.tools.NetTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/5/13.
 */

public class Feedback extends AppCompatActivity {

    private Button button_get_result;
    private TextView textView_show_result;

    private My_Func my_func = new My_Func();
    private NetTool netTool = new NetTool(Feedback.this);

    private ListView listView_project;
    private List<String> list_project = new ArrayList<String>();
    private ArrayAdapter<String> project_adapter;
    private String ProjectName_now;

    private Handler MsgHandler;//消息处理

    private String localAddress;//存储本机ip
    private String locAddrIndex;//存储IP前缀
    private String localDeviceName;//存储本机设备名
    private int j;
    private int IP_connect;
    private ArrayList<String> list_IPs = new ArrayList<String>();
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_result);

        define_palettes();

        Intent intent1_5_6 = getIntent();
        ProjectName_now = intent1_5_6.getStringExtra("ProjectName_now");//读取当前工程名
        makeToast("当前工程为：" + ProjectName_now);

        localAddress = netTool.getLocAddress();//获取本机IP地址
        locAddrIndex = netTool.getLocAddrIndex();

        MsgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String textview_text1=content.replace(" ","  ");
                    String textview_text2=textview_text1.replace("-","--");
                    textView_show_result.setText(textview_text2);

//                    textView_show_result.setText(content);
                }
                if (msg.what == 2) {
                    AlertDialog.Builder AD_error = new AlertDialog.Builder(Feedback.this);
                    AD_error.setMessage("服务端IP地址错误！").create().show();
//                    textView_show_result.setText("服务端IP地址错误！");
//                    textView_show_result.setTextSize(15);
                }
            }
        };
    }

    public void define_palettes() {
        button_get_result = (Button) findViewById(R.id.button_get_result);
        textView_show_result = (TextView) findViewById(R.id.textView_show_result);

//        Animation mAnimationRight;
//        mAnimationRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_right);
//        mAnimationRight.setFillAfter(true);
//
//        textView_show_result.setAnimation(mAnimationRight);
    }

    public void get_result(View v) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //1.创建客户端Socket，指定服务器地址和端口
                    String serviceIP = "10.6.0.54";
                    Socket socket = new Socket(serviceIP, 54321);
                    InputStream is = socket.getInputStream(); // 获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String info = "";
                    while ((info = br.readLine()) != null) {// 循环读取客户端的信息
                        System.out.println("客户端发送过来的信息：" + info);
                        content = content + info + "\n";
                    }
                    if (!content.equals("")) {
                        System.out.println("读取客户端发送过来的信息成功");
                    }
//                    makeToast("接收反馈成功");

                    File ProjectLocation = new File(my_func.get_main_file_path(), ProjectName_now);
                    File file_rt2 = new File(ProjectLocation, ProjectName_now + ".ou2");
                    if (!file_rt2.exists()) {
                        try {
                            file_rt2.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        FileWriter fw = new FileWriter(file_rt2.getAbsoluteFile());
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(content);
                        bw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    socket.shutdownInput();// 关闭输入流
                    socket.close();

                    Message msg1 = new Message();
                    msg1.what = 1;
                    MsgHandler.sendMessage(msg1);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("socket连接失败");

                    Message msg2 = new Message();
                    msg2.what = 2;
                    MsgHandler.sendMessage(msg2);
                }
            }
        }).start();
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
