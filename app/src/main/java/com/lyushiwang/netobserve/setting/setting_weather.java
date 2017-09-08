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
import com.tools.My_Func;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win10 on 2017/4/12.
 */

public class setting_weather extends AppCompatActivity {
    private My_Func my_func = new My_Func();

    private EditText editText_air_pressure;
    private EditText editText_dry_tempe;
    private EditText editText_wet_tempe;
    private EditText editText_weather_quality;
    private Button button_queding;
    private Button button_qingchu;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_weather);

        define_palettes();
        do_click();
    }

    protected void define_palettes() {
        editText_air_pressure = (EditText) findViewById(R.id.editText_air_pressure);
        editText_dry_tempe = (EditText) findViewById(R.id.editText_dry_tempe);
        editText_wet_tempe = (EditText) findViewById(R.id.editText_wet_tempe);
        editText_weather_quality = (EditText) findViewById(R.id.editText_weather_quality);
        button_queding = (Button) findViewById(R.id.button_queding_setting);
        button_qingchu = (Button) findViewById(R.id.button_qingchu_common);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
    }

    protected void do_click() {
        button_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> List_weather = get_and_check_text();
                if (List_weather != null) {
                    File Weather_Parameters = new File(my_func.get_main_file_path(), "Weather Parameters.ini");//气象参数文件
                    Weather_Parameters.delete();
                    try {
                        Weather_Parameters.createNewFile();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(Weather_Parameters, true));
                        for (String item : List_weather) {
                            bw.flush();
                            bw.write(item + "\n");
                            bw.flush();
                        }
                        bw.close();
                        makeToast("设置成功！");
                    } catch (Exception e) {
                        e.printStackTrace();
                        makeToast("Error：无法为Weather Parameters文件创建BufferedWriter!");
                    }
                }else {
                    AlertDialog.Builder AD_check = new AlertDialog.Builder(setting_weather.this);
                    AD_check.setTitle("警告").setMessage("输入有错误，请重新输入！").show();
                }
            }
        });

        button_qingchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_air_pressure.setText("");
                editText_dry_tempe.setText("");
                editText_wet_tempe.setText("");
                editText_weather_quality.setText("");
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
        List_text.add(editText_air_pressure.getText().toString());
        List_text.add(editText_dry_tempe.getText().toString());
        List_text.add(editText_wet_tempe.getText().toString());
        List_text.add(editText_weather_quality.getText().toString());

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

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
