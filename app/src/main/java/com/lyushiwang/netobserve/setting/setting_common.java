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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/4/15.
 */

public class setting_common extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();

    private EditText editText_liangcicha_horizontal;
    private EditText editText_bancehui;
    private EditText editText_yicehui;
    private EditText editText_gecehui_horizontal;
    private EditText editText_liangcicha_vertical;
    private EditText editText_zhibiaocha;
    private EditText editText_gecehui;
    private EditText editText_dushucha;
    private EditText editText_cehuicha;
    private Button button_queding_common;
    private Button button_qingchu_common;
    private ImageButton imageButton_houtui_common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_common);

        define_palettes();

        do_click();
    }

    protected void define_palettes() {
        editText_liangcicha_horizontal = (EditText) findViewById(R.id.editText_liangcicha_horizontal);
        editText_bancehui = (EditText) findViewById(R.id.editText_bancehui);
        editText_yicehui = (EditText) findViewById(R.id.editText_yicehui);
        editText_gecehui_horizontal = (EditText) findViewById(R.id.editText_gecehui_horizontal);
        editText_liangcicha_vertical = (EditText) findViewById(R.id.editText_zhaozhuncha);
        editText_zhibiaocha = (EditText) findViewById(R.id.editText_order);
        editText_gecehui = (EditText) findViewById(R.id.editText_focus_high);
        editText_dushucha = (EditText) findViewById(R.id.editText_dushucha);
        editText_cehuicha = (EditText) findViewById(R.id.editText_cehuicha);
        button_queding_common = (Button) findViewById(R.id.button_queding_setting);
        button_qingchu_common = (Button) findViewById(R.id.button_qingchu_common);
        imageButton_houtui_common = (ImageButton) findViewById(R.id.imageButton_houtui_common);
    }

    protected void do_click() {
        button_queding_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    List<String> List_settings_common = get_and_check_text();
                    if (List_settings_common != null) {
                        File Filepath = my_functions.get_main_file_path();
                        File Common_Settings = new File(Filepath, "Common Settings.ini");//常用参数文件
                        Common_Settings.delete();
                        try {
                            Common_Settings.createNewFile();
                            BufferedWriter bw = new BufferedWriter(new FileWriter(Common_Settings, true));
                            for (String item : List_settings_common) {
                                bw.flush();
                                bw.write(item + "\n");
                                bw.flush();
                            }
                            bw.close();
                            makeToast("设置成功！");
                        } catch (Exception e) {
                            e.printStackTrace();
                            makeToast("Error：无法为Common Settings文件创建BufferedWriter!");
                        }
                    } else {
                        AlertDialog.Builder AD_check = new AlertDialog.Builder(setting_common.this);
                        AD_check.setTitle("警告").setMessage("输入有错误，请重新输入！").show();
                    }
                }
            }
        });

        button_qingchu_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_liangcicha_horizontal.setText("");
                editText_bancehui.setText("");
                editText_yicehui.setText("");
                editText_gecehui_horizontal.setText("");
                editText_liangcicha_vertical.setText("");
                editText_zhibiaocha.setText("");
                editText_gecehui.setText("");
                editText_dushucha.setText("");
                editText_cehuicha.setText("");
            }
        });

        imageButton_houtui_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected List<String> get_and_check_text() {
        List<String> text = new ArrayList<String>();

        text.add(editText_liangcicha_horizontal.getText().toString());
        text.add(editText_bancehui.getText().toString());
        text.add(editText_yicehui.getText().toString());
        text.add(editText_gecehui_horizontal.getText().toString());
        text.add(editText_liangcicha_vertical.getText().toString());
        text.add(editText_zhibiaocha.getText().toString());
        text.add(editText_gecehui.getText().toString());
        text.add(editText_dushucha.getText().toString());
        text.add(editText_cehuicha.getText().toString());

        int error = 0;
        for (String canshu : text) {
            if (canshu.equals("") || canshu.equals(null)) {

                error += 1;
            }
        }
        if (error == 0) {
            return text;
        } else {
            return null;
        }
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
