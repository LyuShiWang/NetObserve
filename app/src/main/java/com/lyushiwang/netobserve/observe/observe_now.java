package com.lyushiwang.netobserve.observe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntegerRes;
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
import com.tools.ClassMeasFunction;
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

    private ClassMeasFunction classmeasFun;//GeoCom
    private BluetoothAdapter BluetoothAdap;// 本地蓝牙适配器
    private boolean bound = false;//存储是否绑定
    //绑定服务的连接
    private ServiceConnection contact_sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClassMeasFunction.LocalBinder binder = (ClassMeasFunction.LocalBinder) service;
            classmeasFun = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            bound = false;
            unbindService(contact_sc);
        }
    }

    //绑定监听服务
    private void bindContactService() {
        Intent intent = new Intent(observe_now.this, ClassMeasFunction.class);
        bindService(intent, contact_sc, BIND_AUTO_CREATE);
    }

    private EditText editText_point_name;
    private EditText editText_station_hight;
    private EditText editText_back_name;
    private EditText editText_front_name;
    private EditText editText_back_hight;
    private EditText editText_front_hight;

    private Button button_input_finish;
    private Button button_observe;
    private Button button_check;
    private Button button_next_point;
    private Button button_save;
    private ImageButton imageButton_houtui;

    private List<ListView_observe_now> list_observe_now = new ArrayList<ListView_observe_now>();
    private ListView listview;

    HashMap<String, Object> map;
    private List<Map<String, Object>> list_listview = new ArrayList<Map<String, Object>>();
    private observe_now.MyAdapter listview_adapter;

    private File file_data = new File(my_functions.get_main_file_path(), "read_data.txt");
    private List<String> list_point_name = new ArrayList<String>();
    private List<Observe_data> list_observe_data = new ArrayList<Observe_data>();

    private Observe_data ob1_back_faceL;
    private Observe_data ob1_front_faceL;
    private Observe_data ob1_front_faceR;
    private Observe_data ob1_back_faceR;

    private int i_tip;

    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_now);

        define_palettes();

        init();
        do_click();
    }

    protected void define_palettes() {
        editText_point_name = (EditText) findViewById(R.id.editText_zhaozhuncha);
        editText_station_hight = (EditText) findViewById(R.id.editText_station_hight);
        editText_back_name = (EditText) findViewById(R.id.editText_back_name);
        editText_front_name = (EditText) findViewById(R.id.editText_front_name);
        editText_back_hight = (EditText) findViewById(R.id.editText_back_hight);
        editText_front_hight = (EditText) findViewById(R.id.editText_gecehui);

        button_input_finish = (Button) findViewById(R.id.button_input_finish);
        button_observe = (Button) findViewById(R.id.button_observe);
        button_check = (Button) findViewById(R.id.button_check);
        button_next_point = (Button) findViewById(R.id.button_next_point);
        button_save = (Button) findViewById(R.id.button_save);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);

        listview = (ListView) findViewById(R.id.listview_observe_now);
    }

    protected void do_click() {
        button_input_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_tip = 1;
                point_face_tip(i_tip);
                i_tip = i_tip + 1;
            }
        });

        button_observe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String station_name = editText_point_name.getText().toString();
                String back_name = editText_back_name.getText().toString();
                String front_name = editText_front_name.getText().toString();
                String[] points_name = {station_name, back_name, front_name};

                if (list_point_name.contains(station_name)) {
                    //开始重测该点
                    AlertDialog.Builder AD_reobserve = new AlertDialog.Builder(observe_now.this);
                    AD_reobserve.setMessage("该点已存在！是否重测该点?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNegativeButton("取消", null).show();
                }
//                read_read_data_txt();

                if (!BluetoothAdap.isEnabled()) {
                    AlertDialog.Builder AD_check_BT = new AlertDialog.Builder(observe_now.this);
                    AD_check_BT.setMessage("未打开蓝牙！请重试").create().show();
                } else {
                    if (i_tip < 5) {
                        String[] strings = null;
                        try {
                            strings = classmeasFun.VB_BAP_MeasDistAng();
//                            map = new HashMap<String, Object>();
//                            map.put("Name", back_name);
//                            map.put("observe_number", "1");
//                            map.put("face_position", "盘左");
//                            map.put("Hz", my_functions.rad2ang_show(ob1_back_faceL.getHz()));
//                            map.put("V", my_functions.rad2ang_show(ob1_back_faceL.getV()));
//                            map.put("S", my_functions.rad2ang_show(ob1_back_faceL.getS()));
//                            list_listview.add(map);
//
//                            listview_adapter = new MyAdapter(observe_now.this, list_listview);
//                            listview.setAdapter(listview_adapter);
//                            listview_adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialog.Builder AD_check_measfun = new AlertDialog.Builder(observe_now.this);
                            AD_check_measfun.setMessage("未连接到蓝牙模块！请重试").create().show();
                        }
                        put_data(i_tip, strings, points_name);
                    }
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                point_face_tip(i_tip);
                i_tip = i_tip + 1;
            }
        });

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_data();
                if (check) {

                } else {
                    AlertDialog.Builder AD_error = new AlertDialog.Builder(observe_now.this);
                    AD_error.setTitle("警告，数据超限！").setPositiveButton("确定", null);
                    //超限的数据类型不同，会有不同的提示内容
                    AD_error.show();
                }
            }
        });

        button_next_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
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

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void do_observe(View v) {

    }


    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void read_read_data_txt() {
        String back_name = editText_back_name.getText().toString();
        String front_name = editText_front_name.getText().toString();
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

    //初始化
    @SuppressLint("NewApi")
    private void init() {
        BluetoothAdap = BluetoothAdapter.getDefaultAdapter();// 获取本地蓝牙适配器
        bindContactService();
        i_tip = 1;
    }

    private void point_face_tip(int i_tip) {
        AlertDialog.Builder AD_tip = new AlertDialog.Builder(observe_now.this);
        String text_tip = "";
        switch (i_tip) {
            case 1:
                AD_tip.setMessage("请观测后视点，并设置为盘左").create().show();
                break;
            case 2:
                AD_tip.setMessage("请观测前视点，并设置为盘左").create().show();
                break;
            case 3:
                AD_tip.setMessage("请观测前视点，并设置为盘右").create().show();
                break;
            case 4:
                AD_tip.setMessage("请观测后视点，并设置为盘右").create().show();
                break;
            default:
                break;
        }
        if (i_tip > 4) {
            AD_tip.setMessage("请对本测回进行数据检查").create().show();
            AD_tip.setMessage("本测回已完成，请进行下一测回或下一测站").create().show();
        }
    }

    private void put_data(int i_tip, String[] strings, String[] points_name) {
//        String[] points_name={station_name,back_name,front_name};
        switch (i_tip) {
            case 1:
                ob1_back_faceL = new Observe_data(points_name[1],
                        strings[1], strings[2], strings[3]);
                break;
            case 2:
                ob1_front_faceL = new Observe_data(points_name[2],
                        strings[1], strings[2], strings[3]);
                break;
            case 3:
                ob1_front_faceR = new Observe_data(points_name[2],
                        strings[1], strings[2], strings[3]);
                break;
            case 4:
                ob1_back_faceR=new Observe_data(points_name[1],
                        strings[1], strings[2], strings[3]);
                break;
        }
    }

    private void check_data() {
        check = true;
        //读取观测限差文件
        File Tolerance_Settings = new File(my_functions.get_main_file_path(), "Tolerance Settings.ini");
        List<String> List_tolerance = new ArrayList<String>();
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(Tolerance_Settings));
            while ((line = br.readLine()) != null) {
                List_tolerance.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error：无法读取Tolerance_Settings文件已有的数据！");
        }
        //1、检查水平角
        int horizontal_zhaozhun=Integer.valueOf(List_tolerance.get(0));
        int horizontal_bancehui=Integer.valueOf(List_tolerance.get(1));
        int horizontal_yicehui= Integer.valueOf(List_tolerance.get(2));
        int horizontal_gecehui=Integer.valueOf(List_tolerance.get(3));
    }
}