package com.lyushiwang.netobserve.observe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyushiwang.netobserve.R;
import com.lyushiwang.netobserve.connect.ConnectRobot;
import com.tools.ClassMeasFunction;
import com.tools.ListView_observe_now;
import com.tools.My_Func;
import com.tools.Observe_data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 吕世望 on 2017/5/1.
 */

public class observe_sort_vertical extends AppCompatActivity {

    private StringBuilder survingString;//存储消息
    private TextView textView_in1_name;
    private TextView textView_in1_text;
    private Button button_get_vert_data;
    private ImageButton imageButton_houtui;

    private My_Func my_func = new My_Func();

    private ClassMeasFunction classmeasFun;//GeoCom
    private BluetoothAdapter BluetoothAdap;// 本地蓝牙适配器
    private boolean bound = false;//存储是否绑定
    //绑定服务的连接
    private ServiceConnection contact_sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClassMeasFunction.LocalBinder binder = (ClassMeasFunction.LocalBinder) service;
            classmeasFun = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            bound = false;
            unbindService(contact_sc);
        }
    }

    //绑定监听服务
    private void bindContactService() {
        Intent intent = new Intent(observe_sort_vertical.this, ClassMeasFunction.class);
        bindService(intent, contact_sc, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_sort_vertical);

        define_palettes();
        init();
        //getData();
        button_get_vert_data.setOnClickListener(listener_get);
    }

    protected void define_palettes(){
        textView_in1_name=(TextView)findViewById(R.id.textView_in1_name);
        textView_in1_text=(TextView)findViewById(R.id.textView_in1_text);
        button_get_vert_data=(Button)findViewById(R.id.button_get_vert_data);
        imageButton_houtui=(ImageButton)findViewById(R.id.imageButton_houtui);
    }

    //初始化
    @SuppressLint("NewApi")
    private void init(){
        BluetoothAdap = BluetoothAdapter.getDefaultAdapter();// 获取本地蓝牙适配器
        bindContactService();
    }

    public void getData(){
        if (!BluetoothAdap.isEnabled()) {
            android.app.AlertDialog.Builder AD_check_BT = new android.app.AlertDialog.Builder(
                    observe_sort_vertical.this);
            AD_check_BT.setMessage("未打开蓝牙！请打开！").create().show();
        } else {
            try{

            } catch (Exception e) {
                e.printStackTrace();
                android.app.AlertDialog.Builder AD_check_measfun = new android.app.AlertDialog.Builder(observe_sort_vertical.this);
                AD_check_measfun.setMessage("未连接到蓝牙模块！请重试")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent_to_bluedevices = new Intent();
                                intent_to_bluedevices.setClass(
                                        observe_sort_vertical.this, ConnectRobot.class);
                                startActivity(intent_to_bluedevices);
                            }
                        }).create().show();
            }
        }
    }

    Button.OnClickListener listener_get=new Button.OnClickListener(){
        public void onClick(View v) {
            if (!BluetoothAdap.isEnabled()) {
                android.app.AlertDialog.Builder AD_check_BT = new android.app.AlertDialog.Builder(
                        observe_sort_vertical.this);
                AlertDialog adg_check_BT= AD_check_BT.setMessage("未打开蓝牙！请打开！").show();
            } else {
                AlertDialog.Builder AD_receive=new AlertDialog.Builder(observe_sort_vertical.this);
                AlertDialog adg_receive= AD_receive.setMessage("正在接受文件...").show();
                try{
                    String data = classmeasFun.receiveData();
                    adg_receive.dismiss();
                    textView_in1_text.setText(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    android.app.AlertDialog.Builder AD_check_measfun = new android.app.AlertDialog.Builder(observe_sort_vertical.this);
                    AD_check_measfun.setMessage("未连接到蓝牙模块！请重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent_to_bluedevices = new Intent();
                                    intent_to_bluedevices.setClass(
                                            observe_sort_vertical.this, ConnectRobot.class);
                                    startActivity(intent_to_bluedevices);
                                }
                            }).create().show();
                }
            }
        }
    };

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
