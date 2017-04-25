package com.lyushiwang.netobserve;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ListView listview;
    private ImageButton imageButton_houtui;

    private File known_points = new File(my_functions.get_main_file_path(), "know points.txt");

    Map<String, Object> map;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_known_point);

        define_palettes();
        do_click();
    }

    protected void define_palettes() {
        editText_point_name = (EditText) findViewById(R.id.editText_point_name);
        editText_point_X = (EditText) findViewById(R.id.editText_point_X);
        editText_point_Y = (EditText) findViewById(R.id.editText_point_Y);
        editText_point_Z = (EditText) findViewById(R.id.editText_point_Z);
        button_add = (Button) findViewById(R.id.button_add);
        button_clear = (Button) findViewById(R.id.button_clear);
        button_delete = (Button) findViewById(R.id.button_delete);
        listview = (ListView) findViewById(R.id.listview_observe_known);
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
                adapter = new MyAdapter(observe_known_point.this, list);
                listview.setAdapter(adapter);

                List<String> List_known_points = get_and_check_text();
                if (List_known_points != null) {
                    map = new HashMap<String, Object>();
                    map.put("Name", List_known_points.get(0).toString());
                    map.put("X_coor",List_known_points.get(1).toString());
                    map.put("Y_coor", List_known_points.get(2).toString());
                    map.put("Z_coor", List_known_points.get(3).toString());
                    list.add(map);
                    adapter.notifyDataSetChanged();
                } else {
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                viewHolder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                viewHolder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                viewHolder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv1.setText(list.get(position).get("Name").toString());
            viewHolder.tv2.setText(list.get(position).get("X_coor").toString());
            viewHolder.tv3.setText(list.get(position).get("Y_coor").toString());
            viewHolder.tv4.setText(list.get(position).get("Z_coor").toString());
            return convertView;
        }

    }

    //辅助类
    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
    }
}