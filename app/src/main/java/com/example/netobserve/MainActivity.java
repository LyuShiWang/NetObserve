package com.example.netobserve;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.lang.String;

public class MainActivity extends Activity {

    private Context mContext;
    private ImageButton gongchengguanli;
    private ImageButton xitongshezhi;
    private ImageButton lianjieshezhi;

    @Override
    //主程序
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();

        //定义控件
        define_palettes();

        File storage_path=Environment.getExternalStorageDirectory();
        File main_file_path=new File(storage_path,"a_NetObserve");
        if (!main_file_path.exists()){
            main_file_path.mkdir();
        }

        //执行点击事件
        do_click();
    }

    protected void define_palettes(){
        gongchengguanli=(ImageButton)findViewById(R.id.imageButton1);
        xitongshezhi=(ImageButton)findViewById(R.id.imageButton2);
        lianjieshezhi=(ImageButton)findViewById(R.id.imageButton3);
    }

    protected void do_click(){
        gongchengguanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"工程管理点击成功！",Toast.LENGTH_LONG).show();
                Intent intent_main2manage=new Intent();
                intent_main2manage.setClass(MainActivity.this, project_manage.class);
                startActivity(intent_main2manage);

            }
        });

        xitongshezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"系统设置点击成功！",Toast.LENGTH_SHORT).show();
                try {
                    final File ProjectNow = new File(get_main_file_path(), "ProjectNow.name");
                    BufferedReader bf=new BufferedReader(new FileReader(ProjectNow));
                    String ProjectName_now=bf.readLine();
                    if(ProjectName_now!=null){
                        Intent intent_main2settings=new Intent();
                        intent_main2settings.setClass(MainActivity.this, system_setting.class);
                        intent_main2settings.putExtra("ProjectName_now",ProjectName_now);
                        startActivity(intent_main2settings);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    makeToast("Error：无法读取ProjectNow文件！");
                }

            }
        });

        lianjieshezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast("连接设置点击成功！");
            }
        });
    }

    public File get_main_file_path(){
        File storage_path=Environment.getExternalStorageDirectory();
        File main_file_path=new File(storage_path,"a_NetObserve");
        return main_file_path;
    }

    protected void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
