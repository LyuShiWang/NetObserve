package com.lyushiwang.netobserve.observe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

/**
 * Created by 吕世望 on 2017/5/1.
 */

public class observe_sort_honrizontal extends AppCompatActivity {
    private My_Func my_func = new My_Func();
    private String ProjectName_now;
    private File file_dist;
    private File file_hza;
    private File file_vca;
    private File file_in2;

    private TextView textView_in2_name;
    private TextView textView_in2_text;
    private ImageButton imageButton_houtui;

    private List<String[]> list_hza_text = new ArrayList<String[]>();
    private List<String[]> list_vca_text = new ArrayList<String[]>();
    private List<String[]> list_dist_text = new ArrayList<String[]>();

    private List<String> list_station_points = new ArrayList<String>();
    private Integer file_size;

    private Handler MsgHandler;//消息处理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_sort_honrizontal);

        AlertDialog.Builder AD_file_handled = new AlertDialog.Builder(observe_sort_honrizontal.this);
        AD_file_handled.setMessage("是否生成.in2平面观测文件?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        define_palettes();
                        init();
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    public void define_palettes() {
        textView_in2_name = (TextView) findViewById(R.id.textView_in2_name);
        textView_in2_text = (TextView) findViewById(R.id.textView_in2_text);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
    }

    public void init() {
        try {
            final File ProjectNow = my_func.get_ProjectNow();
            BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
            ProjectName_now = bf.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error：无法读取ProjectNow文件！");
        }

        file_hza = new File(my_func.get_main_file_path() + "/" + ProjectName_now, ProjectName_now + ".hza");
        file_vca = new File(my_func.get_main_file_path() + "/" + ProjectName_now, ProjectName_now + ".vca");
        file_dist = new File(my_func.get_main_file_path() + "/" + ProjectName_now, ProjectName_now + ".dist");
        if (!file_hza.exists() || !file_vca.exists() || !file_dist.exists()) {
            AlertDialog.Builder AD_error = new AlertDialog.Builder(observe_sort_honrizontal.this);
            AD_error.setTitle("警告").setMessage("文件缺失！无法生成.in2文件！").setPositiveButton("确定", null).create().show();
        }

        file_in2 = new File(my_func.get_main_file_path() + "/" + ProjectName_now, ProjectName_now + ".in2");

        if (file_in2.exists()) {
            AlertDialog.Builder AD_in2exist = new AlertDialog.Builder(observe_sort_honrizontal.this);
            AD_in2exist.setMessage("提示").setMessage(".in2文件已存在！是否删除以生成新的文件？" +
                    "\n按“取消”则打开原有的文件")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                file_in2.delete();
                                file_in2.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            handle_file();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            display_file_in2();
                        }
                    }).create().show();
        } else {//file_in2不存在
            try {
                file_in2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handle_file();
        }
    }

    public void handle_file() {
        //写入全站仪误差参数和已知点坐标
        File file_total_station_tolerance = new File(my_func.get_main_file_path() + "/"
                + ProjectName_now, "total station tolerance.ini");
        File file_known_points = new File(my_func.get_main_file_path() + "/"
                + ProjectName_now, "known points.txt");
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(file_total_station_tolerance));
            BufferedReader br2 = new BufferedReader(new FileReader(file_known_points));
            BufferedWriter bw = new BufferedWriter(new FileWriter(file_in2, true));

            String readline = "";
            String write_text = "";

            while ((readline = br1.readLine()) != null) {
                write_text += readline + ",";
            }
            bw.flush();
            bw.write(write_text.substring(0, write_text.length() - 1) + "\n");
            bw.flush();

            String text_known_points = "";
            while ((readline = br2.readLine()) != null) {
                String[] set_p = readline.split(",");
                text_known_points += set_p[0] + "," + set_p[1] + "," + set_p[2] + "\n";//in2文件中，已知点坐标不能带有Z坐标
            }
            bw.flush();
            bw.write(text_known_points);
            bw.flush();

            bw.close();
            br1.close();
            br2.close();

            //写入数据
            list_hza_text.clear();
            list_vca_text.clear();
            list_dist_text.clear();

            BufferedReader br_Hz = new BufferedReader(new FileReader(file_hza));
            String line_Hz = "";
            while ((line_Hz = br_Hz.readLine()) != null) {
                list_hza_text.add(line_Hz.split(","));
            }
            BufferedReader br_V = new BufferedReader(new FileReader(file_vca));
            String line_V = "";
            while ((line_V = br_V.readLine()) != null) {
                list_vca_text.add(line_V.split(","));
            }
            BufferedReader br_S = new BufferedReader(new FileReader(file_dist));
            String line_S = "";
            while ((line_S = br_S.readLine()) != null) {
                list_dist_text.add(line_S.split(","));
            }

            br_Hz.close();
            br_V.close();
            br_S.close();

            if (list_hza_text.size() == list_vca_text.size() && list_hza_text.size() == list_dist_text.size()) {
                file_size = list_hza_text.size();

                for (int i = 0; i < file_size; i++) {
                    list_station_points.add(list_hza_text.get(i)[0]);
                }
                //去掉重复的元素，使该list中只含有所有测站点
                Set set_station_points = new HashSet();
                set_station_points.addAll(list_station_points);
                list_station_points.clear();
                list_station_points.addAll(set_station_points);

                BufferedWriter bw_in2 = new BufferedWriter(new FileWriter(file_in2, true));//true表示是在原文件上继续写入
                for (String station : list_station_points) {
                    bw_in2.flush();
                    bw_in2.write(station + "\n");
                    bw_in2.flush();

                    for (int i = 0; i < file_size; i++) {
                        if (list_hza_text.get(i)[0].equals(station)) {
                            String focus_point = list_hza_text.get(i)[1];
                            String hza_item = list_hza_text.get(i)[2];//单位：弧度
                            String vca_item = list_vca_text.get(i)[2];//单位：弧度
                            String dist_item = list_dist_text.get(i)[2];//单位：米

                            Double Hz_angle = my_func.rad2ang_show(hza_item);//单位：度.分秒
                            if (Hz_angle < 0.0001) {
                                Hz_angle = 0.0;
                            }
                            bw_in2.flush();
                            bw_in2.write(focus_point + ",L," + Hz_angle + "\n");
                            bw_in2.flush();

                            Double S_distance = Double.valueOf(dist_item) * Math.cos(Double.valueOf(vca_item));
                            S_distance = my_func.baoliu_weishu(S_distance, 6);
                            bw_in2.flush();
                            bw_in2.write(focus_point + ",S," + S_distance + "\n");
                            bw_in2.flush();
                        }
                    }
                }
                bw_in2.close();
            } else {
                makeToast("Error：.hza .vca .dist三个文件的长度不一致！请检查");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        display_file_in2();
    }

    public void display_file_in2() {
        String file_path = file_in2.getAbsolutePath();
        textView_in2_name.setText(file_path);

        String content = "";
        try {
            BufferedReader br_in2 = new BufferedReader(new FileReader(file_in2));
            String line = "";
            while ((line = br_in2.readLine()) != null) {
                content += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView_in2_text.setText(content);
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
