package com.lyushiwang.netobserve;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import android.widget.ImageButton;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.String;
import java.io.File;

public class create_new_project extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();
    private Context mContext;

    private EditText ET_gongchengming;
    private EditText ET_gongchengcanshu;
    private Button Bt_shiyongyicanzai;
    private Button Bt_xinjian;
    private ImageButton IB_houtui;
    private String ProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_project);

        mContext = getApplicationContext();
        define_palettes();
        do_click();
    }

    protected void define_palettes() {
        ET_gongchengming = (EditText) findViewById(R.id.editText_gongchengming);
        ET_gongchengcanshu = (EditText) findViewById(R.id.editText_canshu);
        Bt_shiyongyicanzai = (Button) findViewById(R.id.button_shiyongyicunzai);
        Bt_xinjian = (Button) findViewById(R.id.button_xinjian);
        IB_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
    }

    protected void do_click() {
        Bt_shiyongyicanzai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Bt_xinjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将当前的工程名写入ProjectList文件中
                ProjectName = ET_gongchengming.getText().toString();
                final File ProjectList = my_functions.get_ProjectList();
                try {
                    //true意思是，是否以append的方式写入
                    BufferedWriter bw = new BufferedWriter(new FileWriter(ProjectList, true));
                    bw.flush();
                    bw.write(ProjectName + "\n");
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    my_functions.makeToast("Error：无法将当前的工程名写入ProjectList文件中！");
                }

                //一个工程占据一个文件夹，创建它
                creat_project_directory();
                ET_gongchengming.setText("");
            }
        });

        IB_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void creat_project_directory() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //保存到外部储存的该应用的文件夹内
                File ProjectLocation = new File(my_functions.get_main_file_path(), ProjectName);
                if (!ProjectLocation.exists()) {
                    ProjectLocation.mkdir();
                    create_observe_files(ProjectLocation);//在该工程文件夹内创建必需的测量文件
                    my_functions.makeToast(ProjectName + "已创建！");
                } else {
                    my_functions.makeToast("该工程已存在！");
                }
            } else {
                my_functions.makeToast("SD卡不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            my_functions.makeToast("Error：数据写入失败");
        }
    }

    protected void create_observe_files(File ProjectLocation) {
        try {
            File observe_horizontal = new File(ProjectLocation, ProjectName + ".hza");
            observe_horizontal.createNewFile();
            File observe_vertical = new File(ProjectLocation, ProjectName + ".vca");
            observe_vertical.createNewFile();
            File observe_distance = new File(ProjectLocation, ProjectName + ".dist");
            observe_distance.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            my_functions.makeToast("Error：工程设置文件创建失败！");
        }
    }
}
