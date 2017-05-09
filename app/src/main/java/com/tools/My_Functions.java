package com.tools;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    //status={1,1,1,0,1,1} 空空空非空空
    protected void map_empty(HashMap<String, Object> map, Integer[] status) {
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    if (status[i] == 1) {
                        map.put("Name", "");
                    }
                    break;
                case 1:
                    if (status[i] == 1) {
                        map.put("observe_number", "");
                    }
                    break;
                case 2:
                    if (status[i] == 1) {
                        map.put("face_position", "");
                    }
                    break;
                case 3:
                    if (status[i] == 1) {
                        map.put("Hz", "");
                    }
                    break;
                case 4:
                    if (status[i] == 1) {
                        map.put("V", "");
                    }
                    break;
                case 5:
                    if (status[i] == 1) {
                        map.put("S", "");
                    }
                    break;
            }
        }
    }

    protected void map_putting(HashMap<String, Object> map, int order, String data) {
        switch (order) {
            case 0:
                map.put("Name", data);
                break;
            case 1:
                map.put("observe_number", data);
                break;
            case 2:
                map.put("face_position", data);
                break;
            case 3:
                map.put("Hz", data);
                break;
            case 4:
                map.put("V", data);
                break;
            case 5:
                map.put("S", data);
                break;
        }
    }
}