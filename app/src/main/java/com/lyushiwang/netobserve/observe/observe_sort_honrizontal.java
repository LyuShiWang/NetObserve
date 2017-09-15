package com.lyushiwang.netobserve.observe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lyushiwang.netobserve.R;
import com.tools.My_Func;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_sort_honrizontal);

        AlertDialog.Builder AD_file_handled = new AlertDialog.Builder(observe_sort_honrizontal.this);
        AD_file_handled.setMessage("是否生成.in2平面观测文件?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                init();
                                handle_file();

                                makeToast("生成成功！");
                            }
                        }, 200);
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    public void init() {
        textView_in2_name = (TextView) findViewById(R.id.textView_in2_name);
        textView_in2_text = (TextView) findViewById(R.id.textView_in2_text);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);

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
                        }
                    }).setNegativeButton("取消", null).create().show();
        } else {//file_in2不存在
            try {
                file_in2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

            while ((readline = br2.readLine()) != null) {
                bw.flush();
                bw.write(readline + "\n");
                bw.flush();
            }

            bw.close();
            br1.close();
            br2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean handle_file() {
        try {
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
            while ((line_S = br_Hz.readLine()) != null) {
                list_dist_text.add(line_S.split(","));
            }

            br_Hz.close();
            br_V.close();
            br_S.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list_hza_text.size() == list_vca_text.size() && list_hza_text.size() == list_dist_text.size()) {
            file_size = list_hza_text.size();
        } else {
            makeToast("Error：.hza .vca .dist三个文件的长度不一致！请检查");
        }

        for (int i = 0; i < file_size; i++) {
            list_station_points.add(list_hza_text.get(i)[0]);
        }
        //去掉重复的元素，使该list中只含有所有测站点
        Set set_station_points = new HashSet();
        set_station_points.addAll(list_station_points);
        list_station_points.clear();
        list_station_points.addAll(set_station_points);

        for (String station : list_station_points) {
            try {
                BufferedWriter bw_in2 = new BufferedWriter(new FileWriter(file_in2));
                bw_in2.write(station + "\n");

                for (int i = 0; i < file_size; i++) {
                    if (list_hza_text.get(i)[0].equals(station)) {
                        String focus_point = list_hza_text.get(i)[1];
                        Double Hz_angle = Double.valueOf(list_hza_text.get(i)[2]);
                        String string_Hz_angle = String.valueOf(my_func.rad2ang_show(Hz_angle));
                        bw_in2.write(focus_point + ",L," + string_Hz_angle);


                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
