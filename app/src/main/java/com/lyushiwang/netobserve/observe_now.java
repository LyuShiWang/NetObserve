package com.lyushiwang.netobserve;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 吕世望 on 2017/4/22.
 */

public class observe_now extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();

    private Button button_observe;
    private Button button_next_point;
    private Button button_save;
    private ImageButton imageButton_houtui;

    private List<ListView_observe_now> list_observe_now = new ArrayList<ListView_observe_now>();
    private ListView listview;

    HashMap<String, Object> map;
    private List<Map<String, Object>> list_listview = new ArrayList<Map<String, Object>>();
    private observe_now.MyAdapter listview_adapter;

    private File file_data = new File(my_functions.get_main_file_path(), "read_data.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_now);

        define_palettes();

//        map = new HashMap<String, Object>();
//        map.put("Name", "101");
//        map.put("observe_number", "1");
//        map.put("face_position", "盘左");
//        map.put("Hz", 100.000);//第1行
//        map.put("V", 100.000);//第2行
//        map.put("S", 100.000);//第3行
//        Integer[] empty_status = {0, 0, 0, 1, 1, 1};
//        my_functions.map_empty(map, empty_status);
//        map.put("V", 500.000);//第2行
//
//        list_listview.add(map);
//        listview_adapter = new MyAdapter(observe_now.this, list_listview);
//        listview.setAdapter(listview_adapter);
//        listview_adapter.notifyDataSetChanged();

        do_click();
    }

    protected void define_palettes() {
        button_observe = (Button) findViewById(R.id.button_observe);
        button_next_point = (Button) findViewById(R.id.button_next_point);
        button_save = (Button) findViewById(R.id.button_save);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
        listview = (ListView) findViewById(R.id.listview_observe_now);
    }

    protected void do_click() {
        button_observe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BufferedReader bf = new BufferedReader(new FileReader(file_data));
                    String point_name = bf.readLine();//第0行

//                    map = new HashMap<String, Object>();
//                    my_functions.map_all_empty(map);
//                    map.put("Hz", bf.readLine());
//                    list_listview.add(map);

                    map = new HashMap<String, Object>();
                    map.put("Name", point_name);
                    map.put("observe_number", "1");
                    map.put("face_position", "盘左");
                    map.put("Hz", bf.readLine());//第1行
                    map.put("V", bf.readLine());//第2行
                    map.put("S", bf.readLine());//第3行
                    list_listview.add(map);

                    map = new HashMap<String, Object>();
                    map.put("Name", point_name);
                    map.put("observe_number", "1");
                    map.put("face_position", "盘右");
                    map.put("Hz", bf.readLine());//第4行
                    map.put("V", bf.readLine());//第5行
                    map.put("S", bf.readLine());//第6行
                    list_listview.add(map);

                    map = new HashMap<String, Object>();
                    map.put("Name", point_name);
                    map.put("observe_number", "2");
                    map.put("face_position", "盘左");
                    map.put("Hz", bf.readLine());//第7行
                    map.put("V", bf.readLine());//第8行
                    map.put("S", bf.readLine());//第9行
                    list_listview.add(map);

                    map = new HashMap<String, Object>();
                    map.put("Name", point_name);
                    map.put("observe_number", "2");
                    map.put("face_position", "盘左");
                    map.put("Hz", bf.readLine());//第10行
                    map.put("V", bf.readLine());//第11行
                    map.put("S", bf.readLine());//第12行
                    list_listview.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("read_data文件不存在！");
                }
                listview_adapter = new MyAdapter(observe_now.this, list_listview);
                listview.setAdapter(listview_adapter);
                listview_adapter.notifyDataSetChanged();

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


    //自定义adapter
    public class MyAdapter extends BaseAdapter {
        List<Map<String, Object>> list;
        LayoutInflater inflater;

        public MyAdapter(Context context, List<Map<String, Object>> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            observe_now.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_now, null);
                viewHolder = new observe_now.ViewHolder();
                viewHolder.tv1 = (TextView) convertView.findViewById(R.id.text_name);
                viewHolder.tv2 = (TextView) convertView.findViewById(R.id.text_observe_number);
                viewHolder.tv3 = (TextView) convertView.findViewById(R.id.text_face_position);
                viewHolder.tv4 = (TextView) convertView.findViewById(R.id.text_Hz);
                viewHolder.tv5 = (TextView) convertView.findViewById(R.id.text_V);
                viewHolder.tv6 = (TextView) convertView.findViewById(R.id.text_S);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (observe_now.ViewHolder) convertView.getTag();
            }
            viewHolder.tv1.setText(list.get(position).get("Name").toString());
            viewHolder.tv2.setText(list.get(position).get("observe_number").toString());
            viewHolder.tv3.setText(list.get(position).get("face_position").toString());
            viewHolder.tv4.setText(list.get(position).get("Hz").toString());
            viewHolder.tv5.setText(list.get(position).get("V").toString());
            viewHolder.tv6.setText(list.get(position).get("S").toString());
            return convertView;
        }
    }

    //辅助类
    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        TextView tv5;
        TextView tv6;
    }
}
