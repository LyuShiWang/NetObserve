package com.lyushiwang.netobserve.observe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
    private EditText editText_in1_name;
    private TextView textView_in1_text;
    private Button button_get_vert_data;
    private Button button_check;
    private Button button_tranfer;
    private ImageButton imageButton_houtui;

    private My_Func my_func = new My_Func();
    private  String ProName=new String();

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
        givetip();
        button_check.setOnClickListener(listener_check);
        button_get_vert_data.setOnClickListener(listener_get);
        button_tranfer.setOnClickListener(listener_transfer);
    }

    protected void define_palettes(){
        editText_in1_name=(EditText) findViewById(R.id.editText_in1_name);
        textView_in1_text=(TextView)findViewById(R.id.textView_in1_text);
        button_check=(Button)findViewById(R.id.button_check);
        button_get_vert_data=(Button)findViewById(R.id.button_get_vert_data);
        button_tranfer=(Button)findViewById(R.id.button_transfer);
        imageButton_houtui=(ImageButton)findViewById(R.id.imageButton_houtui);
    }

    //初始化
    @SuppressLint("NewApi")
    private void init(){
        BluetoothAdap = BluetoothAdapter.getDefaultAdapter();// 获取本地蓝牙适配器
        bindContactService();
        ProName=null;
    }

    public void givetip(){
        AlertDialog.Builder AD_tip=new AlertDialog.Builder(observe_sort_vertical.this);
        AD_tip.setTitle("提示").setMessage("输入工程名后，点击“确定”。然后在水准仪上发送数据，提示“接收成功”后点击“获取数据”");
        AlertDialog adg_tip=AD_tip.show();
    }

    Button.OnClickListener listener_check=new Button.OnClickListener(){
        public void onClick(View v){
            ProName=editText_in1_name.getText().toString();
            if (ProName.equals("")||ProName.equals(null)){
                AlertDialog.Builder AD_error=new AlertDialog.Builder(observe_sort_vertical.this);
                AlertDialog adg_error=AD_error.setTitle("警告").setMessage("工程名出错！请检查后重新输入").show();
            }else{
                classmeasFun.CleanData();
                makeToast("输入工程名成功！");
            }
        }
    };

    Button.OnClickListener listener_get=new Button.OnClickListener(){
        public void onClick(View v) {
            if (!BluetoothAdap.isEnabled()) {
                android.app.AlertDialog.Builder AD_check_BT = new android.app.AlertDialog.Builder(
                        observe_sort_vertical.this);
                AlertDialog adg_check_BT= AD_check_BT.setMessage("未打开蓝牙！请打开！").show();
            } else {
                AlertDialog.Builder AD_receive=new AlertDialog.Builder(observe_sort_vertical.this);
                AlertDialog adg_receive= AD_receive.setMessage("正在接收文件...").show();
                String data=new String();
                try{
                    data = classmeasFun.receiveData();
                    adg_receive.dismiss();

                    textView_in1_text.setText("");
                    textView_in1_text.setText(ProName+".GSI\r\n"+data);
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

                if (!data.equals(null)&&!data.equals("")){
                    File filefolder_in1=new File(my_func.get_main_file_path(),ProName);
                    if (!filefolder_in1.exists()){
                        filefolder_in1.mkdir();
                    }

                    File file_in1=new File(my_func.get_main_file_path()+"/"+ProName,
                            ProName+".GSI");
                    try{
                        if (!file_in1.exists()){
                            file_in1.createNewFile();
                        }
                        BufferedWriter bw=new BufferedWriter(new FileWriter(file_in1,true));
                        bw.flush();bw.write(data);bw.flush();bw.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else{
                    AlertDialog.Builder AD_data_error=new AlertDialog.Builder(observe_sort_vertical.this);
                    AlertDialog adg_data_error=AD_data_error.setTitle("警告").setMessage("接收的数据出现问题！").show();
                }
            }
        }
    };

    Button.OnClickListener listener_transfer=new Button.OnClickListener() {
        public void onClick(View v) {
            AlertDialog.Builder AD_transfer=new AlertDialog.Builder(observe_sort_vertical.this);
            AD_transfer.setMessage("是否将"+ProName+".GSI文件转为.in1文件？")
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ProgressDialog PD_transfer=new ProgressDialog(observe_sort_vertical.this);
                }}).show();
        }
    };

    public boolean transfer_data(String GSIFileName){
        boolean istransfered=false;

        return istransfered;
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
