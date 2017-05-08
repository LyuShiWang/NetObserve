package com.lyushiwang.netobserve.observe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyushiwang.netobserve.R;
import com.tools.ListView_observe_now;
import com.tools.My_Functions;
import com.tools.Observe_data;

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

    private EditText editText_point_name;
    private EditText editText_station_hight;
    private EditText editText_back_name;
    private EditText editText_front_name;
    private EditText editText_back_hight;
    private EditText editText_front_hight;

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
    private List<String> list_point_name=new ArrayList<String>();
    private List<Observe_data> list_observe_data = new ArrayList<Observe_data>();

    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_now);

        define_palettes();

        do_click();
    }

    protected void define_palettes() {
        editText_point_name = (EditText) findViewById(R.id.editText_point_name);
        editText_station_hight = (EditText) findViewById(R.id.editText_station_hight);
        editText_back_name = (EditText) findViewById(R.id.editText_back_name);
        editText_front_name = (EditText) findViewById(R.id.editText_front_name);
        editText_back_hight = (EditText) findViewById(R.id.editText_back_hight);
        editText_front_hight = (EditText) findViewById(R.id.editText_front_hight);

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
                String station_name=editText_point_name.getText().toString();
                String back_name = editText_back_name.getText().toString();
                String front_name = editText_front_name.getText().toString();

                if (list_point_name.contains(station_name)){
                    //开始重测该点
                    AlertDialog.Builder AD_reobserve=new AlertDialog.Builder(observe_now.this);
                    AD_reobserve.setMessage("该点已存在！是否重测该点！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNegativeButton("取消",null).show();
                }
                try {
                    BufferedReader bf = new BufferedReader(new FileReader(file_data));

                    Observe_data ob1_back_faceL = new Observe_data(back_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob1_back_faceL);
                    map = new HashMap<String, Object>();
                    map.put("Name", back_name);
                    map.put("observe_number", "1");
                    map.put("face_position", "盘左");
                    map.put("Hz", ob1_back_faceL.getHz_String());//第1行
                    map.put("V", ob1_back_faceL.getV_String());//第2行
                    map.put("S", ob1_back_faceL.getS_String());//第3行
                    list_listview.add(map);

                    Observe_data ob1_front_faceL = new Observe_data(front_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob1_front_faceL);
                    map = new HashMap<String, Object>();
                    map.put("Name", front_name);
                    map.put("observe_number", "1");
                    map.put("face_position", "盘左");
                    map.put("Hz", ob1_front_faceL.getHz_String());//第4行
                    map.put("V", ob1_front_faceL.getV_String());//第5行
                    map.put("S", ob1_front_faceL.getS_String());//第6行
                    list_listview.add(map);

                    Observe_data ob1_front_faceR = new Observe_data(front_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob1_front_faceR);
                    map = new HashMap<String, Object>();
                    map.put("Name", front_name);
                    map.put("observe_number", "1");
                    map.put("face_position", "盘右");
                    map.put("Hz", ob1_front_faceR.getHz_String());//第7行
                    map.put("V", ob1_front_faceR.getV_String());//第8行
                    map.put("S", ob1_front_faceR.getS_String());//第9行
                    list_listview.add(map);

                    Observe_data ob1_back_faceR = new Observe_data(back_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob1_back_faceR);
                    map = new HashMap<String, Object>();
                    map.put("Name", back_name);
                    map.put("observe_number", "1");
                    map.put("face_position", "盘右");
                    map.put("Hz", ob1_back_faceR.getHz_String());//第10行
                    map.put("V", ob1_back_faceR.getV_String());//第11行
                    map.put("S", ob1_back_faceR.getS_String());//第12行
                    list_listview.add(map);

                    Observe_data ob2_back_faceL = new Observe_data(back_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob2_back_faceL);
                    map = new HashMap<String, Object>();
                    map.put("Name", back_name);
                    map.put("observe_number", "2");
                    map.put("face_position", "盘左");
                    map.put("Hz", ob2_back_faceL.getHz_String());//第13行
                    map.put("V", ob2_back_faceL.getV_String());//第14行
                    map.put("S", ob2_back_faceL.getS_String());//第15行
                    list_listview.add(map);

                    Observe_data ob2_front_faceL = new Observe_data(front_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob2_front_faceL);
                    map = new HashMap<String, Object>();
                    map.put("Name", front_name);
                    map.put("observe_number", "2");
                    map.put("face_position", "盘左");
                    map.put("Hz", ob2_front_faceL.getHz_String());//第16行
                    map.put("V", ob2_front_faceL.getV_String());//第17行
                    map.put("S", ob2_front_faceL.getS_String());//第18行
                    list_listview.add(map);

                    Observe_data ob2_front_faceR = new Observe_data(front_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob2_front_faceR);
                    map = new HashMap<String, Object>();
                    map.put("Name", front_name);
                    map.put("observe_number", "2");
                    map.put("face_position", "盘右");
                    map.put("Hz", ob2_front_faceR.getHz_String());//第19行
                    map.put("V", ob2_front_faceR.getV_String());//第20行
                    map.put("S", ob2_front_faceR.getS_String());//第21行
                    list_listview.add(map);

                    Observe_data ob2_back_faceR = new Observe_data(back_name,
                            bf.readLine(), bf.readLine(), bf.readLine());
                    list_observe_data.add(ob2_back_faceR);
                    map = new HashMap<String, Object>();
                    map.put("Name", back_name);
                    map.put("observe_number", "2");
                    map.put("face_position", "盘右");
                    map.put("Hz", ob2_back_faceR.getHz_String());//第22行
                    map.put("V", ob2_back_faceR.getV_String());//第23行
                    map.put("S", ob2_back_faceR.getS_String());//第24行
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

        button_next_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_data();
                if(check){
                    AlertDialog.Builder AD_error=new AlertDialog.Builder(observe_now.this);
                    AD_error.setTitle("警告，数据超限！")
                            .setPositiveButton("确定",null);
                    //超限的数据类型不同，会有不同的提示内容
                    AD_error.show();
                }else{
                    editText_point_name.setText("");
                    editText_station_hight.setText("");
                    editText_back_name.setText("");
                    editText_front_name.setText("");
                    editText_back_hight.setText("");
                    editText_front_hight.setText("");
                }
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

    public void check_data(){
        //检查观测数据是否合格
    }
}
