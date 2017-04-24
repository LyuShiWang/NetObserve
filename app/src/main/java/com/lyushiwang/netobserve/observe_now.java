package com.lyushiwang.netobserve;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕世望 on 2017/4/22.
 */

public class observe_now extends AppCompatActivity {
    private Button button_observe;

    private List<ListView_observe_now> list_observe_now = new ArrayList<ListView_observe_now>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_now);

        define_palettes();

        do_click();

//        //不能用于使用Button来动态增删数据的情景
//        ListView tableListView = (ListView) findViewById(R.id.listview_observe);
//        tableListView.setScrollbarFadingEnabled(true);
//        TableAdapter adapter = new TableAdapter(this, list_observe_now);
//        list_observe_now.add(new ListView_observe_now("101", "1", 168.39182, 168.39182, 199.999));
//        list_observe_now.add(new ListView_observe_now("102", "1", 34, 23, 23));
//        list_observe_now.add(new ListView_observe_now("103", "1", 34.33, 23, 23));
//        list_observe_now.add(new ListView_observe_now("104", "1", 34, 23, 23));
//        list_observe_now.add(new ListView_observe_now("105", "1", 34, 23, 23));
//        list_observe_now.add(new ListView_observe_now("106", "1", 34, 23, 23));
//        list_observe_now.add(new ListView_observe_now("107", "1", 34, 23, 23));
//        list_observe_now.add(new ListView_observe_now("108", "1", 34, 23, 23));
//        list_observe_now.add(new ListView_observe_now("109", "1", 34, 23, 23));
//        tableListView.setAdapter(adapter);

    }

    protected void define_palettes() {
        button_observe = (Button) findViewById(R.id.button_observe);
    }

    protected void do_click(){
        button_observe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
