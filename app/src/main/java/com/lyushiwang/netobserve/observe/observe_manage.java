package com.lyushiwang.netobserve.observe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.lyushiwang.netobserve.R;
import com.lyushiwang.netobserve.functions.My_Functions;

import java.io.File;
import java.lang.String;

/**
 * Created by win10 on 2017/4/21.
 */

public class observe_manage extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();

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
        String ProjectName_now = intent1_4_1.getStringExtra("ProjectName_now");//读取当前工程名
        dangqiangongcheng.setText("当前工程：" + ProjectName_now);
        do_click();
    }

    protected void define_palettes() {
        dangqiangongcheng = (TextView) findViewById(R.id.textview_dangqiangongcheng);
        button_known_point = (Button) findViewById(R.id.button_known_point);
        button_observe = (Button) findViewById(R.id.button_observe);
        button_sort_horizontal = (Button) findViewById(R.id.button_sort_horizontal);
        button_sort_vertical = (Button) findViewById(R.id.button_sort_vertical);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtu);
    }

    protected void do_click() {
        button_known_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file_known_points = new File(my_functions.get_main_file_path(), "known points.txt");
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

        imageButton_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
