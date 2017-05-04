package com.lyushiwang.netobserve.connect;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lyushiwang.netobserve.R;
import com.tools.ClassMeasFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by win10 on 2017/5/4.
 */


public class connect_instrument extends AppCompatActivity {
    private BluetoothAdapter BluetoothAdap;// 本地蓝牙适配器
    private ClassMeasFunction classMeasFunction;
    private UUID MyUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");// UUID
    private BluetoothSocket Socket = classMeasFunction.getSocket();// 通信渠道
    private ListView tvDevices;
    private List<String> bluetoothDevices = new ArrayList<String>();// 存储设备
    private ArrayAdapter<String> arrayAdapter;// 列表适配器
    private BluetoothDevice device;// 蓝牙设备
    private ProgressDialog progressdialog;// 进度对话框
    private Dialog alertDialog;// 连接成功对话框
    private Dialog socketDialog;// 判断连接是否存在的对话框
    //    private ImageView infoOperatingIV;// 旋转图片
//    private Animation operatingAnim;// 旋转
    private Handler handler;// 处理线程
    private Handler MsgHandler;//消息处理
    private HandlerThread thread;// 连接线程
    //    private Bitmap rotateImage;//旋转的图片
    private String instrumentName = "";//设备名字
    private boolean bound = false;//存储是否绑定
//    //绑定服务的连接
//    private ServiceConnection contact_sc = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            ClassMeasFunction.LocalBinder binder = (ClassMeasFunction.LocalBinder) service;
//            classMeasFunction = binder.getService();
//            Socket = classMeasFunction.getSocket();// 获取连接
//            bound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            bound = false;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_instrument);

    }
}
