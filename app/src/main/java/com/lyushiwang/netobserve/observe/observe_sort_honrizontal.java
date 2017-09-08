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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕世望 on 2017/5/1.
 */

public class observe_sort_honrizontal extends AppCompatActivity {
    private My_Func my_func = new My_Func();
    private String ProjectName_now;
    private File file_dist;
    private File file_hza;
    private File file_in2;

    private TextView textView_in2_name;
    private TextView textView_in2_text;
    private ImageButton imageButton_houtui;

    private List<String> list_hza_text = new ArrayList<String>();
    private List<String> list_vca_text = new ArrayList<String>();
    private List<String> list_dist_text = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_sort_honrizontal);

        init();

        handle_file();

        AlertDialog.Builder AD_file_handled = new AlertDialog.Builder(observe_sort_honrizontal.this);
        AD_file_handled.setMessage("生成成功！").setPositiveButton("确定", null).create().show();
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
        file_dist = new File(my_func.get_main_file_path() + "/" + ProjectName_now, ProjectName_now + ".dist");
        if (!file_hza.exists() || !file_dist.exists()) {
            AlertDialog.Builder AD_error = new AlertDialog.Builder(observe_sort_honrizontal.this);
            AD_error.setTitle("警告").setMessage("文件缺失！无法生成.in2文件！").setPositiveButton("确定", null).create().show();
        }

        file_in2 = new File(my_func.get_main_file_path() + "/" + ProjectName_now, ProjectName_now + ".in2");
        try {
            if (file_in2.exists()) {
                AlertDialog.Builder AD_in2exist=new AlertDialog.Builder(observe_sort_honrizontal.this);
                AD_in2exist.setMessage("提示").setMessage(".in2文件已存在！是否删除以生成新的文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    file_in2.delete();
                                    file_in2.createNewFile();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("取消",null).create().show();//取消事件不应该是null，需改进
            }else {//file_in2不存在
                file_in2.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean handle_file() {
        try {
            BufferedReader br_Hz = new BufferedReader(new FileReader(file_hza));
            String line_Hz = "";
            while ((line_Hz = br_Hz.readLine()) != null) {
                list_hza_text.add(line_Hz);
            }
            BufferedReader br_S = new BufferedReader(new FileReader(file_dist));
            String line_S = "";
            while ((line_S = br_Hz.readLine()) != null) {
                list_dist_text.add(line_S);
            }

            br_Hz.close();
            br_S.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
