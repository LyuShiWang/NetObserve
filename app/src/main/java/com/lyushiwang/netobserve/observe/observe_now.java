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
    private EditText editText_focus_name;
    private EditText editText_focus_high;

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

    private List<Observe_data> List_Obdata;

    private String point_guiling;
    private int face;
    private int i_cehuishu;

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
        editText_focus_name = (EditText) findViewById(R.id.editText_focus_name);
        editText_focus_high = (EditText) findViewById(R.id.editText_focus_high);

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
                point_guiling=editText_focus_name.getText().toString();
                i_cehuishu = 1;
                AlertDialog.Builder AD_check_BT = new AlertDialog.Builder(observe_now.this);
                AD_check_BT.setMessage("输入完毕！已将当前照准点设置为归零点，请开始观测。\n" +
                        "此为第" + String.valueOf(i_cehuishu) + "测回")
                        .create().show();
            }
        });

        button_observe.setOnClickListener(listener_observe);

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Observe_data> List_data = new ArrayList<Observe_data>();
                check = check_data(List_data);
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
                    editText_focus_name.setText("");
                    editText_focus_high.setText("");

                    i_cehuishu = 1;//到下一个测站点去测，测回数要进行初始化
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

    Button.OnClickListener listener_observe = new Button.OnClickListener() {
        public void onClick(View v) {

            String station_name = editText_point_name.getText().toString();
            String focus_name = editText_focus_name.getText().toString();
            String[] points_name = {station_name, focus_name};

            if (list_point_name.contains(station_name)) {
//                //开始重测该测站点
//                AlertDialog.Builder AD_reobserve = new AlertDialog.Builder(observe_now.this);
//                AD_reobserve.setMessage("该测站点已存在！是否重测该点?")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }).setNegativeButton("取消", null).show();
            } else {
                if (focus_name.equals(point_guiling)){
                    //回到归零点，一个测回结束
                    AlertDialog.Builder AD_check_BT = new AlertDialog.Builder(observe_now.this);
                    AD_check_BT.setMessage("本次测回已结束！").create().show();
                    face=-face;
                }

                list_point_name.add(station_name);

                if (!BluetoothAdap.isEnabled()) {
                    AlertDialog.Builder AD_check_BT = new AlertDialog.Builder(observe_now.this);
                    AD_check_BT.setMessage("未打开蓝牙！请重试").create().show();
                } else {
                    String[] strings_Total_station = null;
                    try {
                        //数据的结构：第2、3、4分别为水平角、竖直角和距离，单位为弧度、弧度、米
                        strings_Total_station = classmeasFun.VB_BAP_MeasDistAng();

                        List_Obdata.add(put_data_into_Obdata(i_cehuishu, face, points_name, strings_Total_station));

                        //显示在手机屏幕上
                        map = new HashMap<String, Object>();
                        map.put("Name", focus_name);
                        map.put("observe_number", i_cehuishu);
                        map.put("face_position", face_position(face));
                        map.put("Hz", my_functions.rad2ang_show(Double.valueOf(strings_Total_station[1])));
                        map.put("V", my_functions.rad2ang_show(Double.valueOf(strings_Total_station[2])));
                        map.put("S", my_functions.rad2ang_show(Double.valueOf(strings_Total_station[3])));
                        list_listview.add(map);
                        listview_adapter = new MyAdapter(observe_now.this, list_listview);
                        listview.setAdapter(listview_adapter);
                        listview_adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialog.Builder AD_check_measfun = new AlertDialog.Builder(observe_now.this);
                        AD_check_measfun.setMessage("未连接到蓝牙模块！请重试").create().show();
                    }
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//                read_read_data_txt();
        }
    };

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

//    public void read_read_data_txt() {
//        String focus_name=editText_focus_name.getText().toString();
//        try {
//            BufferedReader bf = new BufferedReader(new FileReader(file_data));
//
//            Observe_data ob1_back_faceL = new Observe_data(focus_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob1_back_faceL);
//            map = new HashMap<String, Object>();
//            map.put("Name", focus_name);
//            map.put("observe_number", "1");
//            map.put("face_position", "盘左");
//            map.put("Hz", ob1_back_faceL.getHz_String());//第1行
//            map.put("V", ob1_back_faceL.getV_String());//第2行
//            map.put("S", ob1_back_faceL.getS_String());//第3行
//            list_listview.add(map);
//
//            Observe_data ob1_front_faceL = new Observe_data(front_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob1_front_faceL);
//            map = new HashMap<String, Object>();
//            map.put("Name", front_name);
//            map.put("observe_number", "1");
//            map.put("face_position", "盘左");
//            map.put("Hz", ob1_front_faceL.getHz_String());//第4行
//            map.put("V", ob1_front_faceL.getV_String());//第5行
//            map.put("S", ob1_front_faceL.getS_String());//第6行
//            list_listview.add(map);
//
//            Observe_data ob1_front_faceR = new Observe_data(front_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob1_front_faceR);
//            map = new HashMap<String, Object>();
//            map.put("Name", front_name);
//            map.put("observe_number", "1");
//            map.put("face_position", "盘右");
//            map.put("Hz", ob1_front_faceR.getHz_String());//第7行
//            map.put("V", ob1_front_faceR.getV_String());//第8行
//            map.put("S", ob1_front_faceR.getS_String());//第9行
//            list_listview.add(map);
//
//            Observe_data ob1_back_faceR = new Observe_data(back_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob1_back_faceR);
//            map = new HashMap<String, Object>();
//            map.put("Name", back_name);
//            map.put("observe_number", "1");
//            map.put("face_position", "盘右");
//            map.put("Hz", ob1_back_faceR.getHz_String());//第10行
//            map.put("V", ob1_back_faceR.getV_String());//第11行
//            map.put("S", ob1_back_faceR.getS_String());//第12行
//            list_listview.add(map);
//
//            Observe_data ob2_back_faceL = new Observe_data(back_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob2_back_faceL);
//            map = new HashMap<String, Object>();
//            map.put("Name", back_name);
//            map.put("observe_number", "2");
//            map.put("face_position", "盘左");
//            map.put("Hz", ob2_back_faceL.getHz_String());//第13行
//            map.put("V", ob2_back_faceL.getV_String());//第14行
//            map.put("S", ob2_back_faceL.getS_String());//第15行
//            list_listview.add(map);
//
//            Observe_data ob2_front_faceL = new Observe_data(front_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob2_front_faceL);
//            map = new HashMap<String, Object>();
//            map.put("Name", front_name);
//            map.put("observe_number", "2");
//            map.put("face_position", "盘左");
//            map.put("Hz", ob2_front_faceL.getHz_String());//第16行
//            map.put("V", ob2_front_faceL.getV_String());//第17行
//            map.put("S", ob2_front_faceL.getS_String());//第18行
//            list_listview.add(map);
//
//            Observe_data ob2_front_faceR = new Observe_data(front_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob2_front_faceR);
//            map = new HashMap<String, Object>();
//            map.put("Name", front_name);
//            map.put("observe_number", "2");
//            map.put("face_position", "盘右");
//            map.put("Hz", ob2_front_faceR.getHz_String());//第19行
//            map.put("V", ob2_front_faceR.getV_String());//第20行
//            map.put("S", ob2_front_faceR.getS_String());//第21行
//            list_listview.add(map);
//
//            Observe_data ob2_back_faceR = new Observe_data(back_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_observe_data.add(ob2_back_faceR);
//            map = new HashMap<String, Object>();
//            map.put("Name", back_name);
//            map.put("observe_number", "2");
//            map.put("face_position", "盘右");
//            map.put("Hz", ob2_back_faceR.getHz_String());//第22行
//            map.put("V", ob2_back_faceR.getV_String());//第23行
//            map.put("S", ob2_back_faceR.getS_String());//第24行
//            list_listview.add(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//            makeToast("read_data文件不存在！");
//        }
//    }


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
        check = true;

        AlertDialog.Builder AD_check_BT = new AlertDialog.Builder(observe_now.this);
        AD_check_BT.setMessage("请输入测站点和照准点的信息，然后点击“输入完毕”键")
                .create().show();
    }

//    private void point_face_tip(int i_tip) {
//        AlertDialog.Builder AD_tip = new AlertDialog.Builder(observe_now.this);
//        String text_tip = "";
//        switch (i_tip) {
//            case 1:
//                AD_tip.setMessage("请观测后视点，并设置为盘左").create().show();
//                break;
//            case 2:
//                AD_tip.setMessage("请观测前视点，并设置为盘左").create().show();
//                break;
//            case 3:
//                AD_tip.setMessage("请观测前视点，并设置为盘右").create().show();
//                break;
//            case 4:
//                AD_tip.setMessage("请观测后视点，并设置为盘右").create().show();
//                break;
//            default:
//                break;
//        }
//        if (i_tip > 4) {
//            AD_tip.setMessage("请对本测回进行数据检查").create().show();
//            AD_tip.setMessage("本测回已完成，请进行下一测回或下一测站").create().show();
//        }
//    }

    //    private void put_data(int i_tip, String[] strings, String[] points_name) {
//        String[] points_name={station_name,back_name,front_name};
//        switch (i_tip) {
//            case 1:
//                ob1_back_faceL = new Observe_data(points_name[1],
//                        strings[1], strings[2], strings[3]);
//                break;
//            case 2:
//                ob1_front_faceL = new Observe_data(points_name[2],
//                        strings[1], strings[2], strings[3]);
//                break;
//            case 3:
//                ob1_front_faceR = new Observe_data(points_name[2],
//                        strings[1], strings[2], strings[3]);
//                break;
//            case 4:
//                ob1_back_faceR = new Observe_data(points_name[1],
//                        strings[1], strings[2], strings[3]);
//                break;
//        }
//    }
    private String face_position(int face) {
        String face_pos = "";
        if (face == -1) {
            face_pos = "盘左";
        }
        if (face == 1) {
            face_pos = "盘右";
        }
        return face_pos;
    }

    private Observe_data put_data_into_Obdata(int i_cehuishu, int face,
                                              String[] points_name, String[] strings_Total_station) {
        Observe_data ob_data = new Observe_data();
        ob_data.setCehuishu(i_cehuishu);
        ob_data.setFace(face);
        ob_data.setStationName(points_name[0]);
        ob_data.setFocusName(points_name[1]);
        ob_data.setHz(Double.valueOf(strings_Total_station[1]));
        ob_data.setV(Double.valueOf(strings_Total_station[2]));
        ob_data.setS(Double.valueOf(strings_Total_station[3]));

        return ob_data;
    }

    private boolean check_data(List<Observe_data> List_data) {
        boolean check_data;
        int error = 0;
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

        Double[] double_set_data = {
                List_data.get(0).getHz(), List_data.get(0).getV(), List_data.get(0).getS(),
                List_data.get(1).getHz(), List_data.get(1).getV(), List_data.get(1).getS(),
                List_data.get(2).getHz(), List_data.get(2).getV(), List_data.get(2).getS(),
                List_data.get(3).getHz(), List_data.get(3).getV(), List_data.get(3).getS()
        };

        //1、检查水平角
        int hz_toler_zhaozhun = Integer.valueOf(List_tolerance.get(0));
        int hz_toler_bancehui = Integer.valueOf(List_tolerance.get(1));
        int hz_toler_yicehui = Integer.valueOf(List_tolerance.get(2));
        int hz_toler_gecehui = Integer.valueOf(List_tolerance.get(3));

        //2、检查竖直角
        int v_toler_zhaozhun = Integer.valueOf(List_tolerance.get(4));
        int v_toler_zhibiaocha = Integer.valueOf(List_tolerance.get(5));
        int v_toler_gecehui = Integer.valueOf(List_tolerance.get(6));

        //3、检查距离
        int s_toler_zhaozhun = Integer.valueOf(List_tolerance.get(7));
        int s_toler_gecehui = Integer.valueOf(List_tolerance.get(8));

        if (error > 0) {
            check_data = false;
        } else {
            check_data = true;
        }
        return check_data;
    }
}