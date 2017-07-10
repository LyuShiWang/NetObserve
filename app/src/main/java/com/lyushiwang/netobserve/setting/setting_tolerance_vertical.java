package com.lyushiwang.netobserve.setting;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lyushiwang.netobserve.R;
import com.tools.My_Functions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/4/10.
 */

public class setting_tolerance_vertical extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();

    private EditText editText_liangcicha;
    private EditText editText_zhibiaocha;
    private EditText editText_gecehui;
    private Button button_queding;
    private Button button_qingchu;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_tolerance_vertical);

        define_palettes();

        do_click();
    }

    protected void define_palettes() {
        editText_liangcicha = (EditText) findViewById(R.id.editText_zhaozhuncha);
        editText_zhibiaocha = (EditText) findViewById(R.id.editText_order);
        editText_gecehui = (EditText) findViewById(R.id.editText_gecehui);
        button_queding = (Button) findViewById(R.id.button_queding_setting);
        button_qingchu = (Button) findViewById(R.id.button_qingchu_common);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtu);
    }

    protected void do_click() {
        button_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> List_tolerance_vertical = get_and_check_text();
                if (List_tolerance_vertical != null) {
                    File Tolerance_Settings = new File(my_functions.get_main_file_path(), "Tolerance Settings.ini");//观测限差文件
                    //将旧数据更改为新数据
                    List<String> List_new = vertical_tolerance_change(Tolerance_Settings, List_tolerance_vertical);

                    //将新数据写入文件中
                    Tolerance_Settings.delete();
                    try {
                        Tolerance_Settings.createNewFile();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(Tolerance_Settings, true));
                        for (String item : List_new) {
                            bw.flush();
                            bw.write(item + "\n");
                            bw.flush();
                        }
                        bw.close();
                        makeToast("设置成功！");
                    } catch (Exception e) {
                        e.printStackTrace();
                        makeToast("Error：无法为Tolerance_Settings文件创建BufferedWriter!");
                    }
                }else {
                    AlertDialog.Builder AD_check = new AlertDialog.Builder(setting_tolerance_vertical.this);
                    AD_check.setTitle("警告").setMessage("输入有错误，请重新输入！").show();
                }
            }
        });

        button_qingchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_liangcicha.setText("");
                editText_zhibiaocha.setText("");
                editText_gecehui.setText("");
            }
        });

        imageButton_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public List<String> get_and_check_text() {
        List<String> List_text = new ArrayList<String>();
        List_text.add(editText_liangcicha.getText().toString());
        List_text.add(editText_zhibiaocha.getText().toString());
        List_text.add(editText_gecehui.getText().toString());

        int error = 0;
        for (String item : List_text) {
            if (item.equals("") || item.equals(null)) {
                error += 1;
            }
        }
        if (error == 0) {
            return List_text;
        } else {
            return null;
        }
    }

    public List<String> vertical_tolerance_change(File file, List<String> List_vertical) {
        //存储已存在的数据
        List<String> List_old = new ArrayList<String>();
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                List_old.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error：无法读取Tolerance_Settings文件已有的数据！");
        }

        //用新数据替换旧数据
        List<String> List_new = List_old;
        int line_code = 4;//确定写入的位置
        for (String item : List_vertical) {
            List_new.set(line_code, item);
            line_code += 1;
        }
        return List_new;
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
