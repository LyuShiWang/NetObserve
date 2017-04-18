package com.lyushiwang.netobserve;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by win10 on 2017/4/18.
 */

public class My_Functions extends AppCompatActivity{
    public My_Functions(){

    }

    public void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        return;
    }

    public File get_main_file_path(){
        File storage_path= Environment.getExternalStorageDirectory();
        File main_file_path=new File(storage_path,"a_NetObserve");
        return main_file_path;
    }

    public File get_ProjectList() {
        File file = new File(get_main_file_path(), "ProjectList.list");
        return file;
    }

    public File get_ProjectNow() {
        File file = new File(get_main_file_path(), "ProjectNow.name");
        return file;
    }

    public String read_ProjectNow_Name(File ProjectNow) {
        String ProjectName_now = null;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
            ProjectName_now = bf.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error：无法读取ProjectNow文件！");
        }
        return ProjectName_now;
    }
}
