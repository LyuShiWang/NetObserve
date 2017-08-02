package com.lyushiwang.netobserve.setting;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Context;
import android.widget.Toast;

import com.lyushiwang.netobserve.R;
import com.tools.My_Func;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/4/7.
 */

public class setting_station extends AppCompatActivity {
    private My_Func my_func = new My_Func();
    private Context mContext;

    private EditText editText_observe_number;
    private EditText editText_station_name;
    private EditText editText_group_number;
    private EditText editText_hight;
    private RadioGroup radioGroup_ob_time;
    private RadioButton radioButton_once;
    private RadioButton selectedRadioButton;
    private EditText editText_tolerance;
    private Button button_queding;
    private Button button_qingchu;
    private ImageButton IB_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_station);

        mContext = getApplicationContext();
        define_palettes();
        do_click();
    }

    protected void define_palettes() {
        editText_observe_number = (EditText) findViewById(R.id.editText_observe_number);
        editText_station_name = (EditText) findViewById(R.id.editText_station_name);
        editText_group_number = (EditText) findViewById(R.id.editText_group_number);
        {
            editText_hight = (EditText) findViewById(R.id.editText_hight);
            editText_hight.setKeyListener(new DigitsKeyListener(false, true));
        }
        {
            radioButton_once = (RadioButton) findViewById(R.id.radioButton_once);
            radioGroup_ob_time = (RadioGroup) findViewById(R.id.radiogroup_ob_time);
            radioGroup_ob_time.check(radioButton_once.getId());
        }
        editText_tolerance = (EditText) findViewById(R.id.editText_tolerance);
        button_queding = (Button) findViewById(R.id.button_queding_setting);
        button_qingchu = (Button) findViewById(R.id.button_qingchu_common);
        IB_houtui = (ImageButton) findViewById(R.id.imageButton_seting_station);
    }

    protected void do_click() {
        button_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> List_station_settings = get_and_check_text();
                if (List_station_settings != null) {
                    File Filepath = my_func.get_main_file_path();
                    File Station_Settings = new File(Filepath, "Station Settings.ini");//测站设置文件
                    Station_Settings.delete();
                    try {
                        Station_Settings.createNewFile();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(Station_Settings, true));
                        for (String item : List_station_settings) {
                            bw.flush();
                            bw.write(item + "\n");
                            bw.flush();
                        }
                        bw.close();
                        makeToast("设置成功！");
                    } catch (Exception e) {
                        e.printStackTrace();
                        makeToast("Error：无法为Station_Settings文件创建BufferedWriter!");
                    }
                }else{
                    AlertDialog.Builder AD_check = new AlertDialog.Builder(setting_station.this);
                    AD_check.setTitle("警告").setMessage("输入有错误，请重新输入！").show();
                }
            }
        });

        button_qingchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_observe_number.setText("");
                editText_station_name.setText("");
                editText_group_number.setText("");
                editText_hight.setText("");
                editText_tolerance.setText("");
                makeToast("已清除！");
            }
        });

        IB_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public List<String> get_and_check_text() {
        List<String> text = new ArrayList<String>();

        String observe_number = editText_observe_number.getText().toString();
        text.add(observe_number);
        String station_name = editText_station_name.getText().toString();
        text.add(station_name);
        String group_number = editText_group_number.getText().toString();
        text.add(group_number);
        String high = editText_hight.getText().toString();
        text.add(high);
        {
            int selected = radioGroup_ob_time.getCheckedRadioButtonId();
            selectedRadioButton = (RadioButton) findViewById(selected);
            String Button_Name = selectedRadioButton.getText().toString();
            String observe_time = new String();
            switch (Button_Name) {
                case "一次":
                    observe_time = "1";
                    break;
                case "二次":
                    observe_time = "2";
                    break;
            }
            text.add(observe_time);
        }
        String tolerance = editText_tolerance.getText().toString();
        text.add(tolerance);

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
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}