package com.lyushiwang.netobserve.observe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.lyushiwang.netobserve.R;
import com.tools.My_Func;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;

/**
 * Created by win10 on 2017/4/21.
 */

public class observe_manage extends AppCompatActivity {
    private My_Func my_func = new My_Func();

    private String ProjectName_now;

    private TextView dangqiangongcheng;
    private Button button_known_point;
    private Button button_observe;
    private Button button_sort_horizontal;
    private Button button_sort_vertical;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_manage);

        define_palettes();

        Intent intent1_4_1 = getIntent();
        ProjectName_now = intent1_4_1.getStringExtra("ProjectName_now");//读取当前工程名
        dangqiangongcheng.setText("当前工程：" + ProjectName_now);

        get_total_station_tolerance();
        do_click();
    }

    protected void define_palettes() {
        dangqiangongcheng = (TextView) findViewById(R.id.textview_dangqiangongcheng);
        button_known_point = (Button) findViewById(R.id.button_known_point);
        button_observe = (Button) findViewById(R.id.button_observe);
        button_sort_horizontal = (Button) findViewById(R.id.button_sort_horizontal);
        button_sort_vertical = (Button) findViewById(R.id.button_sort_vertical);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
    }

    protected void do_click() {
        button_known_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file_known_points = new File(my_func.get_main_file_path(), "known points.txt");
                if (!file_known_points.exists()) {
                    try {
                        file_known_points.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        makeToast("无法创建known points文件！");
                    }
                }
                Intent intent_mange2knownPoint = new Intent();
                intent_mange2knownPoint.setClass(observe_manage.this, observe_known_point.class);
                startActivity(intent_mange2knownPoint);
            }
        });
        button_observe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_manage2observe = new Intent();
                intent_manage2observe.setClass(observe_manage.this, observe_now.class);
                startActivity(intent_manage2observe);
            }
        });
        button_sort_horizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder AD_file_in2 = new AlertDialog.Builder(observe_manage.this);
                AD_file_in2.setMessage("是否将观测数据进行水平方向上的整理，生成.in2文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent_plane = new Intent(observe_manage.this, observe_sort_honrizontal.class);
                                startActivity(intent_plane);
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });
        button_sort_vertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder AD_file_in1 = new AlertDialog.Builder(observe_manage.this);
                AD_file_in1.setMessage("是否将观测数据进行竖直方向上的整理，生成.in1文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent_high = new Intent(observe_manage.this, observe_sort_vertical.class);
                                startActivity(intent_high);
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });

        imageButton_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void get_total_station_tolerance() {
        //得到全站仪的方向中误差、测边固定误差、测边比例误差
        final File file_total_station_tolerance = new File(my_func.get_main_file_path() + "/"
                + ProjectName_now, "total station tolerance.ini");

        //提示框
        LayoutInflater factory = LayoutInflater.from(observe_manage.this);
        //这里必须是final的
        final View view_instra_tolerance = factory.inflate(R.layout.simple_alertdialog_edittext, null);
        //获得输入框对象
        final EditText editText_tolerance_angle = (EditText) view_instra_tolerance.findViewById(R.id.editText_tolerance_angle);
        final EditText editText_fixed_tolerance = (EditText) view_instra_tolerance.findViewById(R.id.editText_fixed_tolerance);
        final EditText editText_ratio_tolerance = (EditText) view_instra_tolerance.findViewById(R.id.editText_ratio_tolerance);

        new AlertDialog.Builder(observe_manage.this)
                .setTitle("请输入全站仪限差参数")//提示框标题
                .setView(view_instra_tolerance)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tolerance_angle = editText_tolerance_angle.getText().toString();
                        String fixed_tolerance = editText_fixed_tolerance.getText().toString();
                        String ratio_tolerance = editText_ratio_tolerance.getText().toString();
                        String text = tolerance_angle + "\n" + fixed_tolerance + "\n" + ratio_tolerance;
                        try {
                            if (file_total_station_tolerance.exists()) {
                                file_total_station_tolerance.delete();
                            }
                            file_total_station_tolerance.createNewFile();
                            BufferedWriter bw = new BufferedWriter(
                                    new FileWriter(file_total_station_tolerance, true));
                            bw.flush();
                            bw.write(text);
                            bw.flush();
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
