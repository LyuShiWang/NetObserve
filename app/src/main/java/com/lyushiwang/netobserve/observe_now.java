package com.lyushiwang.netobserve;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕世望 on 2017/4/22.
 */

public class observe_now extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_now);

        define_palettes();

        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);

        List<Points> list = new ArrayList<Points>();
        list.add(new Points("101", "1", 168.39182, 168.39182, 199.999));
        list.add(new Points("102", "1", 34, 23, 23));
        list.add(new Points("103", "1", 34.33, 23, 23));
        list.add(new Points("104", "1", 34, 23, 23));
        list.add(new Points("105", "1", 34, 23, 23));
        list.add(new Points("106", "1", 34, 23, 23));
        list.add(new Points("107", "1", 34, 23, 23));
        list.add(new Points("108", "1", 34, 23, 23));
        list.add(new Points("109", "1", 34, 23, 23));

        ListView tableListView = (ListView) findViewById(R.id.listview);
        tableListView.setScrollbarFadingEnabled(true);
        TableAdapter adapter = new TableAdapter(this, list);

        tableListView.setAdapter(adapter);
    }

    protected void define_palettes(){

    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
