package com.lyushiwang.netobserve;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.lang.String;

/**
 * Created by win10 on 2017/4/21.
 */

public class observe_menu extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();

    private TextView dangqiangongcheng;
    private Button button_observe;
    private Button button_sort_horizontal;
    private Button button_sort_vertical;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_menu);

        define_palettes();

        Intent intent1_4_1 = getIntent();
        String ProjectName_now = intent1_4_1.getStringExtra("ProjectName_now");//读取当前工程名
        dangqiangongcheng.setText("当前工程：" + ProjectName_now);

        do_click();
    }

    protected void define_palettes(){
        dangqiangongcheng=(TextView)findViewById(R.id.textview_dangqiangongcheng);
        button_observe=(Button)findViewById(R.id.button_observe);
        button_sort_horizontal=(Button)findViewById(R.id.button_sort_horizontal);
        button_sort_vertical=(Button)findViewById(R.id.button_sort_vertical);
        imageButton_houtui=(ImageButton)findViewById(R.id.imageButton_houtui);
    }

    protected void do_click(){

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
