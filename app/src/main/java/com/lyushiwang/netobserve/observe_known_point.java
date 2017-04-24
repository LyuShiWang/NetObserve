package com.lyushiwang.netobserve;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕世望 on 2017/4/24.
 */

public class observe_known_point extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();

    private EditText editText_point_name;
    private EditText editText_point_X;
    private EditText editText_point_Y;
    private EditText editText_point_Z;
    private Button button_add;
    private Button button_clear;
    private Button button_delete;
    private ImageButton imageButton_houtui;

    private File known_points = new File(my_functions.get_main_file_path(), "know points.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_known_point);

        define_palettes();

        do_click();

//        list.add(new ListView_observe_now("101", "1", 168.39182, 168.39182, 199.999));
//        list.add(new ListView_observe_now("102", "1", 34, 23, 23));
//        list.add(new ListView_observe_now("103", "1", 34.33, 23, 23));
//        list.add(new ListView_observe_now("104", "1", 34, 23, 23));
//        list.add(new ListView_observe_now("105", "1", 34, 23, 23));
//        list.add(new ListView_observe_now("106", "1", 34, 23, 23));
//        list.add(new ListView_observe_now("107", "1", 34, 23, 23));
//        list.add(new ListView_observe_now("108", "1", 34, 23, 23));
//        list.add(new ListView_observe_now("109", "1", 34, 23, 23));
//
//        ListView tableListView = (ListView) findViewById(R.id.listview_observe_know);
//        tableListView.setScrollbarFadingEnabled(true);
//        TableAdapter adapter = new TableAdapter(this, list);
//
//        tableListView.setAdapter(adapter);
    }

    protected void define_palettes() {
        editText_point_name = (EditText) findViewById(R.id.editText_point_name);
        editText_point_X = (EditText) findViewById(R.id.editText_point_X);
        editText_point_Y = (EditText) findViewById(R.id.editText_point_Y);
        editText_point_Z = (EditText) findViewById(R.id.editText_point_Z);
        button_add = (Button) findViewById(R.id.button_add);
        button_clear = (Button) findViewById(R.id.button_clear);
        button_delete = (Button) findViewById(R.id.button_delete);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
    }

    public List<String> get_and_check_text() {
        List<String> List_text = new ArrayList<String>();
        List_text.add(editText_point_name.getText().toString());
        List_text.add(editText_point_X.getText().toString());
        List_text.add(editText_point_Y.getText().toString());
        List_text.add(editText_point_Z.getText().toString());

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

    protected void do_click() {
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> List_known_points=get_and_check_text();
                if (List_known_points!=null){

                }else {
                    AlertDialog.Builder AD_check = new AlertDialog.Builder(observe_known_point.this);
                    AD_check.setTitle("警告");
                    AD_check.setMessage("输入有错误，请重新输入！");
                    AD_check.show();
                }
            }
        });
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_point_name.setText("");
                editText_point_X.setText("");
                editText_point_Y.setText("");
                editText_point_Z.setText("");
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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