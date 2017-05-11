package com.lyushiwang.netobserve.connect;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.lyushiwang.netobserve.R;
import com.tools.ClassMeasFunction;
import com.tools.My_Functions;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * Created by win10 on 2017/5/4.
 */

public class ConnectRobot extends AppCompatActivity implements OnItemClickListener {
    private My_Functions myFunctions = new My_Functions();

    private BluetoothAdapter BluetoothAdap;// 本地蓝牙适配器
    private ClassMeasFunction classMeasFunction = new ClassMeasFunction();
    private BluetoothSocket Socket = classMeasFunction.getSocket();// 通信渠道
    private UUID MyUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");// UUID
    private ListView tvDevices;
    private Button button_interact;
    private List<String> list_bluetoothDevices = new ArrayList<String>();// 存储设备
    private ArrayAdapter<String> arrayAdapter;// 列表适配器
    private BluetoothDevice device;// 蓝牙设备
    private ProgressDialog progressdialog;// 进度对话框
    private Dialog alertDialog;// 连接成功对话框
    private Dialog socketDialog;// 判断连接是否存在的对话框
    private ImageView infoOperatingIV;// 旋转图片
    private Animation operatingAnim;// 旋转
    private Handler handler;// 处理线程
    private Handler MsgHandler;//消息处理
    private HandlerThread thread;// 连接线程
    private Bitmap rotateImage;//旋转的图片
    private String instrumentName = "";//设备名字
    private boolean bound = false;//存储是否绑定
    //    private ContactApp app;// 存储共享数据
    //绑定服务的连接
    private ServiceConnection contact_sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClassMeasFunction.LocalBinder binder = (ClassMeasFunction.LocalBinder) service;
            classMeasFunction = binder.getService();
            Socket = classMeasFunction.getSocket();// 获取连接
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_robot);

        init();
    }

    @Override
    protected void onDestroy() {
        rotateImage.recycle();//释放图片资源
        if (bound) {
            bound = false;
            unbindService(contact_sc);
        }
        super.onDestroy();
    }

    //初始化
    @SuppressLint("NewApi")
    private void init() {
        setFinishOnTouchOutside(false);
        setTitle(getString(R.string.connectRobot));//设置标题
        tvDevices = (ListView) findViewById(R.id.allDeviceList);// 存储设备的列表
        button_interact = (Button) findViewById(R.id.button_interact);
        BluetoothAdap = BluetoothAdapter.getDefaultAdapter();// 获取本地蓝牙适配器

        handler = new Handler();
        MsgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1)
                    progressdialog.dismiss();
                if (msg.what == 2)
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        Set<BluetoothDevice> pairedAdapters = BluetoothAdap.getBondedDevices();// 设备集合

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);// 发现广播
        this.registerReceiver(receiver, filter);// 注册广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);// 停止搜索
        this.registerReceiver(receiver, filter);// 注册广播
        if (pairedAdapters.size() > 0)// 添加以配对设备
        {
            for (BluetoothDevice device : pairedAdapters) {
                list_bluetoothDevices.add(device.getName() + "\n" + device.getAddress());// 添加绑定设备
            }
        } else {
            makeToast("未发现设备！");
        }

        rotateImage = BitmapFactory.decodeResource(getResources(), R.drawable.refresh_black);
//         旋转图片
        infoOperatingIV = (ImageView) findViewById(R.id.infoOperating);
        infoOperatingIV.setImageBitmap(rotateImage);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setDuration(1000);
        operatingAnim.setInterpolator(lin);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list_bluetoothDevices);
        tvDevices.setAdapter(arrayAdapter);
        tvDevices.setOnItemClickListener(this);

        progressdialog = new ProgressDialog(ConnectRobot.this);
        // 设置进度条风格，风格为圆形，旋转的
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题
        progressdialog.setTitle("提示");
        progressdialog.setCanceledOnTouchOutside(false);

        // 设置ProgressDialog 提示信息
        progressdialog.setMessage("正在连接设备，请稍等.....");
        progressdialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Socket.isConnected())// 已经连接
                        {
                            alertDialog = new AlertDialog.Builder(ConnectRobot.this)
                                    .setTitle("提示")
                                    .setMessage("连接成功")
                                    .setPositiveButton("确定",
                                            new OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    alertDialog.dismiss();
                                                    finish();
                                                }
                                            }).create();
                            alertDialog.show();
                        } else {
                            alertDialog = new AlertDialog.Builder(ConnectRobot.this)
                                    .setTitle("提示")
                                    .setMessage("连接失败")
                                    .setPositiveButton("确定", new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            handler.removeCallbacks(mRunnable);// 销毁线程
                                            finish();
                                        }
                                    }).create();
                            alertDialog.show();
                            handler.removeCallbacks(mRunnable);// 销毁线程
                        }
                    }
                });
        bindContactService();
    }

    //绑定监听服务
    private void bindContactService() {
        Intent intent = new Intent(ConnectRobot.this, ClassMeasFunction.class);
        bindService(intent, contact_sc, BIND_AUTO_CREATE);
    }

    // 扫描设备
    public void scanDevice(View v) {
        if (BluetoothAdap.isDiscovering() == true) {
            BluetoothAdap.cancelDiscovery();// 停止搜索
        } else {
            BluetoothAdap.startDiscovery();// 开始搜索
            if (operatingAnim != null) {
                infoOperatingIV.startAnimation(operatingAnim);
            }
        }
    }

    //交互
    public void interact(View v) {
//        BluetoothAdapter temp2=BluetoothAdap;
//        ClassMeasFunction temp3=classMeasFunction;
//        BluetoothSocket temp1=classMeasFunction.getSocket();
            makeToast("已连接！");
            String GetAngle = myFunctions.strings2string(classMeasFunction.TMC_GetAngle());
            String MeasDistAng = myFunctions.strings2string(classMeasFunction.VB_BAP_MeasDistAng());
//            //VB_BAP_MeasDistAng()的原始结构：[0,水平角（弧度）,竖直角（弧度）,斜距（单位：米m）,2]
//
            String text = "measdisang: " + MeasDistAng + "\n";
            AlertDialog.Builder AD_interact = new AlertDialog.Builder(ConnectRobot.this);
            AD_interact.setMessage(text).setPositiveButton("确定", null).create().show();

    }

    //连接设备
    public void setSocket() {
        Socket = classMeasFunction.getSocket();// 获取连接
        progressdialog.show();
        thread = new HandlerThread("MyHandlerThread");
        thread.start();// 创建一个HandlerThread并启动它
        handler = new Handler(thread.getLooper());// 使用HandlerThread的looper对象创建Handler，
        // 如果使用默认的构造方法，很有可能阻塞UI线程
        handler.post(mRunnable);// 将线程post到Handler中
    }

    @SuppressLint("NewApi")
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = arrayAdapter.getItem(position);
        String[] split = s.split("\n");
        String address = split[1];// 获得设备地址
        instrumentName = split[0];//设备名字
        try {
            if (BluetoothAdap.isDiscovering() == true) {
                BluetoothAdap.cancelDiscovery();// 停止扫描
            }
            try {
                if (Socket == null) {
                    device = BluetoothAdap.getRemoteDevice(address);
                    setSocket();
                } else {
                    if (Socket.isConnected()) {
                        device = BluetoothAdap.getRemoteDevice(address);
                        socketDialog = new AlertDialog.Builder(
                                ConnectRobot.this)
                                .setTitle("提示")
                                .setMessage("该设备已连接一个设备，是否断开该连接并重新连接新的设备？")
                                .setPositiveButton("是", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Socket.close();
                                            Socket = null;// 至空
                                            classMeasFunction.closeSocket();// 至空连接
                                            socketDialog.dismiss();
                                            setSocket();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("否", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        socketDialog.dismiss();
                                    }
                                }).create();
                        socketDialog.show();
                    } else {
                        Socket = null;// 至空
                        classMeasFunction.closeSocket();// 至空连接
                        socketDialog.dismiss();
                        setSocket();
                    }
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {// 定义蓝牙广播接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);// 获取远程是设备
                // 将设备名称和地址放入array adapter，以便在ListView中显示
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String information = device.getName() + "\n" + device.getAddress();
                    if (list_bluetoothDevices.contains(information) == false) {
                        list_bluetoothDevices.add(information);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                operatingAnim.cancel();
                infoOperatingIV.clearAnimation();// 停止旋转
                makeToast("停止搜索");
            }
        }

    };

    // 连接线程
    private Runnable mRunnable = new Runnable() {
        public void run() {
            try {
                Socket = device.createRfcommSocketToServiceRecord(MyUUID);
                Socket.connect();
                classMeasFunction.init(Socket);
//                app.setSerialPortOpen(true);// 设置共享数据
//                File file = new File(app.getOpenedStationPath()
//                        + "/sendAndReceive.txt");
//                FileWriter writer = new FileWriter(file);
                classMeasFunction.beginComutting();
//                app.setInstrumentNO(instrumentName);
//                Properties station_pro = new Properties();
//                File file4 = new File(app.getOpenedStationPath() + "/StationSetting.ini");
//                FileReader fileReader2 = new FileReader(file4);
//                station_pro.load(fileReader2);
//                fileReader2.close();
//                station_pro.setProperty("RobotNO", instrumentName);//设仪器号
//                FileWriter writer1 = new FileWriter(file4);
//                station_pro.store(writer1, "");
//                writer1.close();
                Message msg2 = new Message();
                msg2.what = 1;
                MsgHandler.sendMessage(msg2);
            } catch (IOException e) {
            }
        }
    };

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}