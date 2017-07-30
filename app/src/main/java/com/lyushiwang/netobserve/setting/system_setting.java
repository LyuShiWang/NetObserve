package com.lyushiwang.netobserve.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Context;

import com.lyushiwang.netobserve.R;
import com.tools.My_Functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.String;

public class system_setting extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();
    private Context mContext;

    private TextView dangqiangongcheng;
    private Button cezhanshezhi;
    private Button xianchashezhi;
    private Button qixiangcanshu;
    private Button changyongcanshu;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_setting);

        define_palettes();

        mContext = getApplicationContext();
        Intent intent1_2_1 = getIntent();
        String ProjectName_now = intent1_2_1.getStringExtra("ProjectName_now");//读取当前工程名
        dangqiangongcheng.setText("当前工程：" + ProjectName_now);
        create_setting_files();//如果系统文件（.ini）不存在，则创建它们
        //.ini位于程序总目录内，不属于任何工程

        do_click(ProjectName_now);
    }

    protected void define_palettes() {
        dangqiangongcheng = (TextView) findViewById(R.id.textview_dangqiangongcheng);
        cezhanshezhi = (Button) findViewById(R.id.button_observe);
        xianchashezhi = (Button) findViewById(R.id.button_xianchashezhi);
        qixiangcanshu = (Button) findViewById(R.id.button_qixiangcanshu);
        changyongcanshu = (Button) findViewById(R.id.button_changyongcanshu);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtu);
    }

    protected void create_setting_files() {
        File Filepath = my_functions.get_main_file_path();

        File Station_Settings = new File(Filepath, "Station Settings.ini");//测站设置文件
        if (!Station_Settings.exists()) {
            try {
                Station_Settings.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File Tolerance_Settings = new File(Filepath, "Tolerance Settings.ini");//观测限差文件
        if (!Tolerance_Settings.exists()) {
            try {
                Tolerance_Settings.createNewFile();
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(Tolerance_Settings));
                    for (int i = 0; i < 9; i++) {//一共有九个参数
                        bw.flush();
                        bw.write("0\n");
                        bw.flush();
                    }
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Error：无法为Tolerance_Settings文件写入初始数据！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File Weather_Parameters = new File(Filepath, "Weather Parameters.ini");//气象参数文件
        if (!Weather_Parameters.exists()) {
            try {
                Weather_Parameters.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File Common_Settings = new File(Filepath, "Common Settings.ini");//常用参数文件
        if (!Common_Settings.exists()) {
            try {
                Common_Settings.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void do_click(final String ProjectName_now) {
        cezhanshezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_setting2station = new Intent();
                intent_setting2station.setClass(system_setting.this, setting_station.class);
                intent_setting2station.putExtra("ProjectName_now", ProjectName_now);
                startActivity(intent_setting2station);
            }
        });

        xianchashezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder AD_tolerance_choice = new AlertDialog.Builder(system_setting.this);
                final String[] choice = {"水平角", "竖直角", "距离"};
                AD_tolerance_choice.setTitle("请选择进行何种设置")
                        .setItems(choice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent_setting2tolerance = new Intent();
                                switch (which) {
                                    case 0:
                                        intent_setting2tolerance.setClass(system_setting.this, setting_tolerance_horizontal.class);
                                        break;
                                    case 1:
                                        intent_setting2tolerance.setClass(system_setting.this, setting_tolerance_vertical.class);
                                        break;
                                    case 2:
                                        intent_setting2tolerance.setClass(system_setting.this, setting_tolerance_distance.class);
                                        break;
                                }
                                startActivity(intent_setting2tolerance);
                            }
                        }).show();
            }
        });

        qixiangcanshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_setting2weather = new Intent();
                intent_setting2weather.setClass(system_setting.this, setting_weather.class);
                startActivity(intent_setting2weather);
            }
        });

        changyongcanshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_setting2common = new Intent();
                intent_setting2common.setClass(system_setting.this, setting_common.class);
                startActivity(intent_setting2common);
            }
        });

        imageButton_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
