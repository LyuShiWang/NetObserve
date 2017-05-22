package com.lyushiwang.netobserve;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.content.Context;

import com.lyushiwang.netobserve.connect.ConnectRobot;
import com.lyushiwang.netobserve.observe.observe_now;
import com.tools.ClassMeasFunction;
import com.tools.My_Functions;
import com.lyushiwang.netobserve.manage.project_manage;
import com.lyushiwang.netobserve.observe.observe_manage;
import com.lyushiwang.netobserve.setting.system_setting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.lang.String;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();
    private Context mContext;
    private BluetoothAdapter BluetoothAdap;// 本地蓝牙适配器

    private ImageButton gongchengguanli;
    private ImageButton xitongshezhi;
    private ImageButton lianjieshezhi;
    private ImageButton guance;
    private ImageButton chakanshuju;
    private ImageButton shangchuanshuju;
    private ImageButton jieguofankui;
    private ImageButton tuichu;

    private ClassMeasFunction classmeasFun;//GeoCom
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
        Intent intent = new Intent(MainActivity.this, ClassMeasFunction.class);
        bindService(intent, contact_sc, BIND_AUTO_CREATE);
    }

    @Override
    //主程序
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        //定义控件
        define_palettes();

        File storage_path = Environment.getExternalStorageDirectory();
        File main_file_path = new File(storage_path, "a_NetObserve");
        if (!main_file_path.exists()) {
            main_file_path.mkdir();
        }
        bindContactService();
        //执行点击事件
        do_click();
    }

    protected void define_palettes() {
        gongchengguanli = (ImageButton) findViewById(R.id.imageButton1);
        xitongshezhi = (ImageButton) findViewById(R.id.imageButton2);
        lianjieshezhi = (ImageButton) findViewById(R.id.imageButton3);
        guance = (ImageButton) findViewById(R.id.imageButton4);
        chakanshuju = (ImageButton) findViewById(R.id.imageButton5);
        shangchuanshuju=(ImageButton)findViewById(R.id.imageButton6);
        jieguofankui=(ImageButton)findViewById(R.id.imageButton7);

        tuichu = (ImageButton) findViewById(R.id.imageButton8);
    }

    protected void do_click() {
        gongchengguanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_main2manage = new Intent();
                intent_main2manage.setClass(MainActivity.this, project_manage.class);
                startActivity(intent_main2manage);
            }
        });

        xitongshezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final File ProjectNow = new File(my_functions.get_main_file_path(), "ProjectNow.name");
                    BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
                    String ProjectName_now = bf.readLine();
                    if (ProjectName_now != null) {
                        Intent intent_main2settings = new Intent();
                        intent_main2settings.setClass(MainActivity.this, system_setting.class);
                        intent_main2settings.putExtra("ProjectName_now", ProjectName_now);
                        startActivity(intent_main2settings);
                    } else {
                        makeToast("还未打开工程！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Error：无法读取ProjectNow文件！");
                }

            }
        });

        lianjieshezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdap=BluetoothAdapter.getDefaultAdapter();
                if(BluetoothAdap.isEnabled()){
                    Intent intent_main2connect = new Intent();
                    intent_main2connect.setClass(MainActivity.this, ConnectRobot.class);
                    startActivity(intent_main2connect);
                }else {
                    makeToast("还未打开蓝牙！请打开后再进入配对界面");
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                }
            }
        });

        guance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final File ProjectNow = new File(my_functions.get_main_file_path(), "ProjectNow.name");
                    BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
                    String ProjectName_now = bf.readLine();
                    if (ProjectName_now != null) {
                        Intent intent_main2observe = new Intent();
                        intent_main2observe.setClass(MainActivity.this, observe_manage.class);
                        intent_main2observe.putExtra("ProjectName_now", ProjectName_now);
                        startActivity(intent_main2observe);
                    } else {
                        makeToast("还未打开工程！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Error：无法读取ProjectNow文件！");
                }
            }
        });

        chakanshuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        shangchuanshuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_main2upload=new Intent();
                intent_main2upload.setClass(MainActivity.this,UploadData.class);
                startActivity(intent_main2upload);
            }
        });

        jieguofankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final File ProjectNow = new File(my_functions.get_main_file_path(), "ProjectNow.name");
                    BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
                    String ProjectName_now = bf.readLine();
                    if (ProjectName_now != null) {
                        Intent intent_main2feedback=new Intent();
                        intent_main2feedback.putExtra("ProjectName_now", ProjectName_now);
                        intent_main2feedback.setClass(MainActivity.this,Feedback.class);
                        startActivity(intent_main2feedback);
                    } else {
                        makeToast("还未打开工程！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Error：无法读取ProjectNow文件！");
                }
            }
        });

        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
