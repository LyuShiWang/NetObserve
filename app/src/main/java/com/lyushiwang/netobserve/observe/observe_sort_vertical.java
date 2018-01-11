package com.lyushiwang.netobserve.observe;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.lyushiwang.netobserve.R;
import com.tools.ClassMeasFunction;
import com.tools.My_Func;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lyushiwang.netobserve.R;

import org.w3c.dom.Text;

/**
 * Created by 吕世望 on 2017/5/1.
 */

public class observe_sort_vertical extends AppCompatActivity {
    private TextView textView_in1_name;
    private TextView textView_in1_text;
    private Button button_get_vert_data;
    private ImageButton imageButton_houtui;

//    private My_Func my_func = new My_Func();
//
//    private ClassMeasFunction classmeasFun;//GeoCom
//    private BluetoothAdapter BluetoothAdap;// 本地蓝牙适配器
//    private boolean bound = false;//存储是否绑定
//    //绑定服务的连接
//    private ServiceConnection contact_sc = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            ClassMeasFunction.LocalBinder binder = (ClassMeasFunction.LocalBinder) service;
//            classmeasFun = binder.getService();
//            bound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            bound = false;
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (bound) {
//            bound = false;
//            unbindService(contact_sc);
//        }
//    }
//
//    //绑定监听服务
//    private void bindContactService() {
//        Intent intent = new Intent(observe_sort_vertical.this, ClassMeasFunction.class);
//        bindService(intent, contact_sc, BIND_AUTO_CREATE);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_sort_vertical);

//        define_palettes();
//        init();
//        button_get_vert_data.setOnClickListener(listener_get);
    }

//    protected void define_palettes(){
//        textView_in1_name=(TextView)findViewById(R.id.textView_in1_name);
//        textView_in1_text=(TextView)findViewById(R.id.textView_in1_text);
//        button_get_vert_data=(Button)findViewById(R.id.button_get_vert_data);
//        imageButton_houtui=(ImageButton)findViewById(R.id.imageButton_houtui);
//    }
//
//    protected void init(){
//        BluetoothAdap = BluetoothAdapter.getDefaultAdapter();// 获取本地蓝牙适配器
//        bindContactService();
//    }
//
//    Button.OnClickListener listener_get=new Button.OnClickListener(){
//        public void onClick(View v) {
//            if (!BluetoothAdap.isEnabled()) {
//                android.app.AlertDialog.Builder AD_check_BT = new android.app.AlertDialog.Builder(
//                        observe_sort_vertical.this);
//                AD_check_BT.setMessage("未打开蓝牙！请打开！").create().show();
//            } else {
//                String[] number=classmeasFun.getInstrumentNo();
//            }
//        }
//    };
}
