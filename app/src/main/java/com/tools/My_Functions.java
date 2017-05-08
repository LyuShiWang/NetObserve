package com.tools;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by win10 on 2017/4/18.
 */

public class My_Functions {
    public My_Functions() {

    }

    public File get_main_file_path() {
        File storage_path = Environment.getExternalStorageDirectory();
        File main_file_path = new File(storage_path, "a_NetObserve");
        return main_file_path;
    }

    public File get_ProjectList() {
        File fileList = new File(get_main_file_path(), "ProjectList.list");
        return fileList;
    }

    public File get_ProjectNow() {
        File fileNow = new File(get_main_file_path(), "ProjectNow.name");
        return fileNow;
    }

    public String read_ProjectNow_Name(File ProjectNow) {
        String ProjectName_now = null;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
            ProjectName_now = bf.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProjectName_now;
    }

    protected void map_all_empty(HashMap<String, Object> map) {
        map.put("Name", "");
        map.put("observe_number", "");
        map.put("face_position", "");
        map.put("Hz", "");
        map.put("V", "");
        map.put("S", "");
    }

}