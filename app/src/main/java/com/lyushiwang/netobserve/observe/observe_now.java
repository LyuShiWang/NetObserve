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
import android.view.inputmethod.InputMethodManager;
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
import com.tools.My_Func;
import com.tools.Observe_data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 吕世望 on 2017/4/22.
 */

public class observe_now extends AppCompatActivity {
    private My_Func my_func = new My_Func();

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

    private EditText editText_station_name;
    private EditText editText_station_hight;
    private EditText editText_focus_name;
    private EditText editText_focus_high;

    private TextView textView_tips;

    private Button button_observe;
    private Button button_undo;
    private Button button_next_cehui;
    private Button button_next_point;
    private Button button_save;
    private ImageButton imageButton_houtui;

    private List<ListView_observe_now> list_observe_now = new ArrayList<ListView_observe_now>();
    private ListView listview;

    HashMap<String, Object> map;
    private List<Map<String, Object>> list_listview = new ArrayList<Map<String, Object>>();
    private observe_now.MyAdapter listview_adapter;

    //    private File file_data = new File(my_func.get_main_file_path(), "read_data.txt");
    private String ProjectName_now;
    private String station_name;
    private File file_in2;
    private File file_ob_data;

    private List<String> list_station_points = new ArrayList<String>();
    private List<String> list_focus_points = new ArrayList<String>();
    private List<String> list_focus_1_round = new ArrayList<String>();
    private List<String> list_order_name_LEFT = new ArrayList<String>();
    private List<String> list_order_name_RIGHT = new ArrayList<String>();

    private List<Observe_data> list_Obdata = new ArrayList<Observe_data>();
    //这个list_Obdata只储存一个测站的观测数据。该测站的数据合格后，写入txt文件中，并清空该List

    //1、水平角限差，单位：秒″
    private int hz_toler_zhaozhun;
    private int hz_toler_guiling;//水平角半测回归零差
    private int hz_toler_2C;//水平角一测回2C互差
    private int hz_toler_gecehui;//水平角同方向各测回互差

    //2、竖直角限差，单位：秒″
    private int v_toler_zhaozhun;
    private int v_toler_zhibiaocha;//竖直角一测回指标差互差
    private int v_toler_gecehui;//竖直角同方向各测回互差

    //3、距离限差，单位：毫米mm
    private int s_toler_zhaozhun;
    private int s_toler_gecehui;//距离同方向各测回互差

    private List<Double> Hz_2C = new ArrayList<Double>();
    private List<Double> V_zhibiaocha = new ArrayList<Double>();

    private List<Double[]> calculate_Hz = new ArrayList<Double[]>();
    private List<Double[]> calculate_V = new ArrayList<Double[]>();
    private List<Double[]> calculate_S = new ArrayList<Double[]>();

    private List<Double> Hz_bencehui = new ArrayList<Double>();
    private List<Double> V_bencehui = new ArrayList<Double>();
    private List<Double> S_bencehui = new ArrayList<Double>();

    private String face;
    private int i_cehuishu;
    private int i_focus_points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_now);

        define_palettes();

        init();
        do_click();
    }

    protected void define_palettes() {
        editText_station_name = (EditText) findViewById(R.id.editText_zhaozhuncha);
        editText_station_hight = (EditText) findViewById(R.id.editText_station_hight);
        editText_focus_name = (EditText) findViewById(R.id.editText_focus_name);
        editText_focus_high = (EditText) findViewById(R.id.editText_focus_high);

        textView_tips = (TextView) findViewById(R.id.textView_tips);

        button_observe = (Button) findViewById(R.id.button_observe);
        button_undo = (Button) findViewById(R.id.button_undo);
        button_save = (Button) findViewById(R.id.button_save);
        button_next_cehui = (Button) findViewById(R.id.button_next_cehui);
        button_next_point = (Button) findViewById(R.id.button_next_point);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);

        listview = (ListView) findViewById(R.id.listview_observe_now);
    }

    protected void do_click() {
        button_observe.setOnClickListener(listener_observe);

        button_next_cehui.setOnClickListener(listener_next_cehui);

        button_next_point.setOnClickListener(listener_next_point);

        button_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //撤销最近的一条观测数据
                AlertDialog.Builder AD_undo = new AlertDialog.Builder(observe_now.this);
                AD_undo.setMessage("是否撤销最近的一次观测数据？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                undo();
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageButton_houtui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder AD_finish = new AlertDialog.Builder(observe_now.this);
                AD_finish.setMessage("是否确定退出观测？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });
    }

    Button.OnClickListener listener_observe = new Button.OnClickListener() {
        public void onClick(View v) {
            textView_tips.setText("正在观测......");
            station_name = editText_station_name.getText().toString();
            String focus_name = editText_focus_name.getText().toString();
            if (station_name.equals("")) {
                textView_tips.setText("请输入测站点名！");
            } else {
                if (list_station_points.contains(station_name)) {
                    AlertDialog.Builder AD_reobserve = new AlertDialog.Builder(observe_now.this);
                    AD_reobserve.setMessage("该测站点已存在！\n是否对其进行重新观测？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    list_station_points.remove(station_name);
                                    clear_station();
                                    textView_tips.setText("请开始观测！");
                                }
                            }).create().show();
                } else {
                    if (!BluetoothAdap.isEnabled()) {
                        AlertDialog.Builder AD_check_BT = new AlertDialog.Builder(observe_now.this);
                        AD_check_BT.setMessage("未打开蓝牙！请重试").create().show();
                    } else {
                        try {
                            //数据的结构：第2、3、4分别为水平角、竖直角和距离，单位为弧度、弧度、米
                            String[] strings_Total_station = classmeasFun.VB_BAP_MeasDistAng();
                            if (!check_observe_data(strings_Total_station)) {
                                AlertDialog.Builder AD_observe_error = new AlertDialog.Builder(observe_now.this);
                                AD_observe_error.setMessage("数据异常！请重新照准目标棱镜！").create().show();
                            } else {
                                Double V_face = Double.valueOf(strings_Total_station[2]);
                                if (V_face < Math.PI) {
                                    face = "LEFT";
                                }
                                if (V_face > Math.PI) {
                                    face = "RIGHT";
                                }

                                String[] points_name = get_points_name_set(station_name);
                                if (!points_name[1].equals("NULL")) {
                                    //存储数据，及屏幕显示
                                    put_and_display_and_save(i_cehuishu, points_name, strings_Total_station);
                                    if (face.equals("LEFT")) {
                                        //计算一共本测站一共观测了多少个目标点
                                        if (i_cehuishu == 1) {
                                            i_focus_points += 1;
                                            list_order_name_LEFT.add(focus_name);
                                            list_order_name_RIGHT.add(focus_name);
                                            textView_tips.setText("第" + String.valueOf(i_focus_points) +
                                                    "个点盘左观测成功！\n请观测下一个点");
                                        } else {
                                            textView_tips.setText("第"
                                                    + String.valueOf(i_focus_points - list_order_name_LEFT.size())
                                                    + "个点盘左观测成功！\n请观测下一个点");
                                        }

                                    } else {
                                        int size_RIGHT = list_order_name_RIGHT.size();
                                        if (size_RIGHT > 0) {
                                            textView_tips.setText("第" + String.valueOf(size_RIGHT + 1) +
                                                    "个点盘右观测成功！\n请观测下一个点");
                                        } else {
                                            textView_tips.setText("盘右观测完毕！\n" +
                                                    "请选择进行下一测回，或下一测站");
                                        }
                                    }
                                    if (i_cehuishu == 1) {
                                        list_focus_1_round.add(points_name[1]);
                                    }
                                } else {
                                    AlertDialog.Builder AD_error_edittext_focus =
                                            new AlertDialog.Builder(observe_now.this);
                                    AD_error_edittext_focus.setTitle("警告")
                                            .setMessage("未输入照准点名！请输入后再进行观测").create().show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialog.Builder AD_check_measfun = new AlertDialog.Builder(observe_now.this);
                            AD_check_measfun.setMessage("未连接到蓝牙模块！请重试").create().show();
                        }
                    }
                }
            }
        }
    };

    Button.OnClickListener listener_next_cehui = new Button.OnClickListener() {
        public void onClick(View v) {
            List<Integer> list_error_1_round = check_data_round_end(list_Obdata);
            if (list_error_1_round.size() == 0) {
                AlertDialog.Builder AD_check_measfun = new AlertDialog.Builder(observe_now.this);
                AD_check_measfun.setMessage("本测回数据合格！\n是否进入下一测回？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                join_listview_data();
                                i_cehuishu += 1;
                                textView_tips.setText("已进入第" + String.valueOf(i_cehuishu) + "测回！\n" +
                                        "请对准初始照准点，然后点击“观测”键");
                                editText_focus_name.setText("");
                                editText_focus_high.setText("");

                                list_order_name_LEFT.clear();
                                list_order_name_RIGHT.clear();
                                for (int i = 0; i < i_focus_points; i++) {
                                    list_order_name_LEFT.add(list_focus_1_round.get(i));
                                    list_order_name_RIGHT.add(list_focus_1_round.get(i));
                                }
                                //慎用List类的.subList方法
                            }
                        }).create().show();
            } else {
                textView_tips.setText("本测回数据超限！请及时处理");
//                if (list_error_1_round.contains(1)) {
//                    AlertDialog.Builder AD_error_face_right = new AlertDialog.Builder(observe_now.this);
//                    AD_error_face_right.setMessage("盘右水平角归零差超限！\n请重新观测！").create().show();
//                }
                if (list_error_1_round.contains(3)) {
                    AlertDialog.Builder AD_error_2C = new AlertDialog.Builder(observe_now.this);
                    AD_error_2C.setMessage("本测回水平角2C互差超限！\n请重新观测！").create().show();
                }
                if (list_error_1_round.contains(5)) {
                    AlertDialog.Builder AD_error_zhibiaocha = new AlertDialog.Builder(observe_now.this);
                    AD_error_zhibiaocha.setMessage("本测回竖直角指标差超限！\n请重新观测！").create().show();
                }
            }
        }
    };

    Button.OnClickListener listener_next_point = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Integer> list_error_1_round = check_data_round_end(list_Obdata);
            if (list_error_1_round.size() == 0) {
                AlertDialog.Builder AD_check_measfun = new AlertDialog.Builder(observe_now.this);
                AD_check_measfun.setMessage("本测站数据合格！\n是否进入下一测站？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list_listview.clear();
                                listview_adapter = new MyAdapter(observe_now.this, list_listview);
                                listview.setAdapter(listview_adapter);
                                listview_adapter.notifyDataSetChanged();
                                next_station();
                            }
                        }).create().show();
            } else {
                textView_tips.setText("本测站数据超限！请及时处理");
            }
        }
    };

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

//    public void read_read_data_txt() {
//        String focus_name=editText_focus_name.getText().toString();
//        try {
//            BufferedReader bf = new BufferedReader(new FileReader(file_data));
//
//            Observe_data ob1_back_faceL = new Observe_data(focus_name,
//                    bf.readLine(), bf.readLine(), bf.readLine());
//            list_data_read.add(ob1_back_faceL);
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
//            list_data_read.add(ob1_front_faceL);
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
//            list_data_read.add(ob1_front_faceR);
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
//            list_data_read.add(ob1_back_faceR);
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
//            list_data_read.add(ob2_back_faceL);
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
//            list_data_read.add(ob2_front_faceL);
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
//            list_data_read.add(ob2_front_faceR);
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
//            list_data_read.add(ob2_back_faceR);
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
        i_cehuishu = 1;
        i_focus_points = 0;

        read_observe_tolerance();
        get_in2_and_write_total_torelance();

        file_ob_data = new File(my_func.get_main_file_path() + "/" + ProjectName_now
                , ProjectName_now + ".ob");
        try {
            if (!file_ob_data.exists()) {
                file_ob_data.createNewFile();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(file_ob_data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView_tips.setText("请输入测站点和初始照准点的信息，然后点击“观测”键");
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

    public String[] get_points_name_set(String station_name) {
        //用于处理照准点点名，主要是进行在盘右观测时和第一测回之后的点名自动设置
        String[] points_name = new String[2];
        points_name[0] = station_name;
        String focus_name = null;

        if (face.equals("LEFT")) {
            //盘左观测，如果是第一测回，则记录点名
            //没有填写点名则自动报错
            //如果不是第一测回，则自动填写点名
            if (i_cehuishu == 1) {
                focus_name = editText_focus_name.getText().toString();
                if (!focus_name.equals("")) {
                    points_name[1] = focus_name;
                    list_focus_points.add(focus_name);

                    editText_focus_name.setFocusable(true);
                    editText_focus_name.requestFocus();//将光标移动到该edittext上
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {//弹出输入法
                            InputMethodManager inputManager = (InputMethodManager) editText_focus_name.getContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.showSoftInput(editText_focus_name, 0);
                        }

                    }, 250);
                } else {
                    points_name[1] = "NULL";
                }
            } else {//i_cehuishu != 1
                int size = list_order_name_LEFT.size();
                if (size > 0) {
                    focus_name = list_order_name_LEFT.get(0);
                    points_name[1] = focus_name;
                    editText_focus_name.setText(focus_name);
                    list_order_name_LEFT.remove(0);
                }
            }
        }
        if (face.equals("RIGHT")) {
            //盘右观测，不需要手动输点名，按盘左输入点名的逆顺序，自动输入点名
            //可能会出现照错点的情况，需要进行检查
            int size = list_order_name_RIGHT.size();
            if (size > 0) {
                focus_name = list_order_name_RIGHT.get(size - 1);
                points_name[1] = focus_name;
                editText_focus_name.setText(focus_name);
                list_order_name_RIGHT.remove(size - 1);
            }
        }
        return points_name;
    }

    public void put_and_display_and_save(
            int i_cehuishu, String[] points_name, String[] strings_Total_station) {
        Observe_data observe_data = put_data_into_Obdata(i_cehuishu, face, points_name, strings_Total_station);
        list_Obdata.add(observe_data);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file_ob_data, true));
            bw.flush();
            bw.write(observe_data.toFileString() + "\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //显示在手机屏幕上
        map = new HashMap<String, Object>();
        map.put("Name", points_name[1]);
        map.put("observe_number", i_cehuishu);
        map.put("face_position", face_position(face));
        map.put("Hz", my_func.rad2ang_show(Double.valueOf(strings_Total_station[1])));
        map.put("V", my_func.rad2ang_show(Double.valueOf(strings_Total_station[2])));
        map.put("S", my_func.baoliu_weishu(strings_Total_station[3], 3));
        list_listview.add(map);
        listview_adapter = new MyAdapter(observe_now.this, list_listview);
        listview.setAdapter(listview_adapter);
        listview_adapter.notifyDataSetChanged();

        if (face.equals("LEFT")) {
            if (i_cehuishu == 1) {
                editText_focus_name.setText("");
            }
        }
    }

    public Observe_data put_data_into_Obdata(int i_cehuishu, String face,
                                             String[] points_name, String[] strings_Total_station) {
        //结构：测回数，盘位，测站点，照准点，水平角，竖直角，斜距
        Observe_data ob_data = new Observe_data();
        ob_data.setCehuishu(i_cehuishu);
        ob_data.setFace(face);
        ob_data.setStationName(points_name[0]);
        ob_data.setFocusName(points_name[1]);
        ob_data.setHz(Double.valueOf(strings_Total_station[1]));//单位：弧度
        ob_data.setV(Double.valueOf(strings_Total_station[2]));//单位：弧度
        ob_data.setS(Double.valueOf(strings_Total_station[3]));//单位：米

        return ob_data;
    }

    public String face_position(String face) {
        String face_pos = "";
        if (face.equals("LEFT")) {
            face_pos = "盘左";
        }
        if (face.equals("RIGHT")) {
            face_pos = "盘右";
        }
        return face_pos;
    }

    public void join_listview_data() {
        //删除原始观测数据
        for (int i = 0; i < 2 * i_focus_points; i++) {
            list_listview.remove(list_listview.size() - 1);
        }

        for (int j = 0; j < i_focus_points; j++) {
            map = new HashMap<String, Object>();
            map.put("Name", list_focus_1_round.get(j));
            map.put("observe_number", i_cehuishu);
            map.put("face_position", "");
            map.put("Hz", my_func.rad2ang_show(calculate_Hz.get(i_cehuishu - 1)[j]));
            map.put("V", my_func.rad2ang_show(calculate_V.get(i_cehuishu - 1)[j]));
            map.put("S", my_func.baoliu_weishu(calculate_S.get(i_cehuishu - 1)[j], 3));
            list_listview.add(map);
        }

        listview_adapter = new MyAdapter(observe_now.this, list_listview);
        listview.setAdapter(listview_adapter);
        listview_adapter.notifyDataSetChanged();
    }

    public void undo() {
        if (list_Obdata.size() > 0) {
            //从手机屏幕上撤销
            list_listview.remove(list_listview.get(list_listview.size() - 1));
            listview_adapter = new MyAdapter(observe_now.this, list_listview);
            listview.setAdapter(listview_adapter);
            listview_adapter.notifyDataSetChanged();

            list_Obdata.remove(list_Obdata.size() - 1);

            list_focus_points.remove(list_focus_points.size() - 1);

            if (i_cehuishu == 1) {
                if (face.equals("LEFT")) {
                    i_focus_points -= 1;
                    list_order_name_RIGHT.remove(list_order_name_RIGHT.size() - 1);
                } else {
                    //保证盘右观测时自动填入的点名不出错
                    int size_RIGHT = list_order_name_RIGHT.size();
                    list_order_name_RIGHT.add(list_focus_1_round.get(i_focus_points + size_RIGHT - 1));
                }
                list_focus_1_round.remove(list_focus_1_round.size() - 1);
            } else {// i_cehuishu != 1
                if (face.equals("LEFT")) {
                    int size_LEFT = list_order_name_LEFT.size();
                    list_order_name_LEFT.add(0, list_focus_1_round.get(i_focus_points - size_LEFT - 1));
                } else {// face.equals("RIGHT")
                    int size_RIGHT = list_order_name_RIGHT.size();
                    list_order_name_RIGHT.add(list_focus_1_round.get(size_RIGHT));
                }
            }

            textView_tips.setText("撤回成功！请再次观测该点！");
        } else {
            AlertDialog.Builder AD_undo_error = new AlertDialog.Builder(observe_now.this);
            AD_undo_error.setMessage("没有可以撤销的数据！").create().show();
        }
    }

    //进行重测
//        String station_name = editText_station_name.getText().toString();
//        if (list_station_points.contains(station_name)) {
//            AlertDialog.Builder AD_reobserve = new AlertDialog.Builder(observe_now.this);
//            AD_reobserve.setMessage("该测站点已存在！是否重测？")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //假设是刚测完就进行重测，删除list_Obdata最近的四条数据即可
//                            int list_size = list_Obdata.size();
//                            for (int i = 0; i < 4; i++) {
//                                list_Obdata.remove(list_size - i);
//                            }
//                        }
//                    }).setNegativeButton("取消", null).create().show();
//        }
//        else {}
    public void clear_station() {
        list_focus_points.clear();
//        list_sub_focus.clear();
        list_order_name_RIGHT.clear();
        list_Obdata.clear();
        i_cehuishu = 1;
        i_focus_points = 0;
    }

    public boolean check_data_round_left(List<Observe_data> List_data) {
        boolean is_checked;
        int error = 0;

        int first = 2 * (i_cehuishu - 1) * (i_focus_points + 1);
        int sencond = 2 * (i_cehuishu - 1) * (i_focus_points + 1) + i_focus_points;
        int third = (2 * i_cehuishu - 1) * (i_focus_points + 1);
        int forth = (2 * i_cehuishu - 1) * (i_focus_points + 1) + i_focus_points;

//        if (i_focus_points > 2) {
//            //检查盘左半测回归零差
//            Double Hz_0_LEFT = List_data.get(first).getHz();
//            Double Hz_end_LEFT = List_data.get(sencond).getHz();
//            Double guilingcha = my_func.rad2ang_show(Hz_end_LEFT - Hz_0_LEFT) * 100 * 100;//单位：秒″
//
//            if (guilingcha > hz_toler_guiling) {
//                //该半测回归零差超限
//                error += 1;
//            }
//        }

        if (error == 0) {
            is_checked = true;
        } else {
            is_checked = false;
        }
        return is_checked;
    }

    public boolean check_observe_data(String[] strings_Total_station) {
        boolean ischecked = true;

        Double Hz = Double.valueOf(strings_Total_station[1]);//单位：弧度
        Double V = Double.valueOf(strings_Total_station[2]);//单位：弧度
        if (Hz > 2 * Math.PI) {
            ischecked = false;
        }
        if (V > 2 * Math.PI) {
            ischecked = false;
        }
        return ischecked;
    }

    public List<Integer> check_data_round_end(List<Observe_data> List_data) {
        boolean is_checked;
        List<Integer> list_error = new ArrayList<Integer>();

        //需要归零
//        int first = 2 * (i_cehuishu - 1) * (i_focus_points + 1);
//        int sencond = 2 * (i_cehuishu - 1) * (i_focus_points + 1) + i_focus_points;
//        int third = (2 * i_cehuishu - 1) * (i_focus_points + 1);
//        int forth = (2 * i_cehuishu - 1) * (i_focus_points + 1) + i_focus_points;

        //不归零
        int first = 2 * (i_cehuishu - 1) * i_focus_points;
        int sencond = (2 * i_cehuishu - 1) * i_focus_points - 1;
        int third = (2 * i_cehuishu - 1) * i_focus_points;
        int forth = 2 * i_cehuishu * i_focus_points - 1;

        //进行一测回内的检查
//        if (i_focus_points > 2) {
//            //检查盘右半测回归零差
//            Double Hz_0_LEFT = List_data.get(third).getHz();
//            Double Hz_end_LEFT = List_data.get(forth).getHz();//单位：弧度
//            Double guilingcha = my_func.rad2ang_show(Hz_end_LEFT - Hz_0_LEFT) * 100 * 100;//单位：秒″
//            if (guilingcha > hz_toler_guiling) {
//                //该半测回归零差超限
//                list_error.add(1);
//            }
//        }

        //进行一测回各方向Hz的2C互差检查
        Hz_2C.clear();
        for (int i = 0; i < i_focus_points; i++) {
            Double Hz_2C_1 = List_data.get(first + i).getHz();
            Double Hz_2C_4 = List_data.get(forth - i).getHz();//单位：弧度
            Double two_C;
            if (Hz_2C_4 > Math.PI) {
                two_C = Hz_2C_1 - (Hz_2C_4 - Math.PI);//单位：弧度
            } else {
                two_C = Hz_2C_1 - (Hz_2C_4 + Math.PI);//单位：弧度
            }
            two_C = my_func.rad2ang(two_C) * 60 * 60;//单位：秒 ″
            Hz_2C.add(two_C);//单位：秒 ″
        }
        Double delta_2C = Collections.max(Hz_2C) - Collections.min(Hz_2C);//单位：秒 ″
        if (delta_2C > hz_toler_2C) {
            //该测回2C互差超限
            list_error.add(3);
        }

        //进行一测回各方向V指标差互差检查
        V_zhibiaocha.clear();
        for (int i = 0; i < i_focus_points; i++) {
            Double V_zhibiaocha_1 = List_data.get(first + i).getV();
            Double V_zhibiaocha_4 = List_data.get(forth - i).getV();//单位：弧度
            Double zhibiaocha;
            zhibiaocha = (V_zhibiaocha_1 + V_zhibiaocha_4 - 2 * Math.PI) / (double) 2;//单位：弧度
            zhibiaocha = my_func.rad2ang(zhibiaocha) * 60 * 60;//单位：秒 ″
            V_zhibiaocha.add(zhibiaocha);
        }
        Double delta_zhibiaocha = Collections.max(V_zhibiaocha) - Collections.min(V_zhibiaocha);
        if (delta_zhibiaocha > v_toler_zhibiaocha) {
            //该测回指标差互差超限
            list_error.add(5);
        }

        if (list_error.size() == 0) {
            //计算本测回各方向Hz值
            Double[] Hz_set = new Double[i_focus_points];
            Hz_set[0] = List_data.get(first).getHz();//单位：弧度

            Double Hz_LEFT_0 = List_data.get(first).getHz();
            Double Hz_RIGHT_0 = List_data.get(forth).getHz();
            for (int i = 1; i < i_focus_points; i++) {
                Double Hz_LEFT = List_data.get(first + i).getHz();
                Double Hz_RIGHT = List_data.get(forth - i).getHz();//单位：弧度

                Double Hz_focus = (Hz_LEFT - Hz_LEFT_0 + Hz_RIGHT - Hz_RIGHT_0) / (double) 2;//单位：弧度
                if (Hz_focus < 0) {
                    Hz_focus += 2 * Math.PI;
                }
                if (Hz_focus > 2 * Math.PI) {
                    Hz_focus -= 2 * Math.PI;
                }
                Hz_set[i] = Hz_focus;
            }
            calculate_Hz.add(Hz_set);//单位：弧度

            //计算本测回各方向V值
            Double[] V_set = new Double[i_focus_points];
            for (int i = 0; i < i_focus_points; i++) {
                Double V_LEFT = List_data.get(first + i).getV();
                Double V_RIGHT = List_data.get(forth - i).getV();//单位：弧度

                Double V_focus = (V_RIGHT - V_LEFT - Math.PI) / (double) 2;//单位：弧度
                V_set[i] = V_focus;
            }
            calculate_V.add(V_set);//单位：弧度

            //计算本测回各方向S值
            Double[] S_set = new Double[i_focus_points];
            for (int i = 0; i < i_focus_points; i++) {
                Double S_LEFT = List_data.get(first + i).getS();
                Double S_RIGHT = List_data.get(forth - i).getS();//单位：米

                Double S_focus = (S_LEFT + S_RIGHT) / (double) 2;
                S_set[i] = S_focus;
            }
            calculate_S.add(S_set);
        } else {
        }
        return list_error;
    }

    public List<String[]> check_gecehui(List<Observe_data> List_data) {
        List<String[]> list_error_fangxiang = new ArrayList<String[]>();
        String[] error_set = new String[2];

        //进行测回间的Hz互差检查
        error_set = new String[2];
        ArrayList<Double> Hz_gecehui = new ArrayList<Double>();
        for (int i = 0; i < i_focus_points; i++) {
            for (int j = 0; j < i_cehuishu; j++) {
                Hz_gecehui.add(calculate_Hz.get(j)[i]);
            }
            Double delta_Hz = Collections.max(Hz_gecehui) - Collections.min(Hz_gecehui);//单位：弧度
            delta_Hz = my_func.rad2ang_show(delta_Hz);//单位：秒 ″
            if (delta_Hz > hz_toler_gecehui) {
                error_set[0] = "Hz";
                error_set[1] = list_focus_1_round.get(i);
                list_error_fangxiang.add(error_set);
            }
        }

        //进行测回间的V互差检查
        error_set = new String[2];
        List<Double> V_gecehui = new ArrayList<Double>();
        for (int i = 0; i < i_focus_points; i++) {
            for (int j = 0; j < i_cehuishu; j++) {
                V_gecehui.add(calculate_V.get(j)[i]);
            }
            Double delta_V = Collections.max(V_gecehui) - Collections.min(V_gecehui);//单位：弧度
            delta_V = my_func.rad2ang_show(delta_V) * 100 * 100;//单位：秒 ″
            if (delta_V > v_toler_gecehui) {
                error_set[0] = "V";
                error_set[1] = list_focus_1_round.get(i);
                list_error_fangxiang.add(error_set);
            }
        }

        //进行测回间的S互差检查
        error_set = new String[2];
        List<Double> S_gecehui = new ArrayList<Double>();
        for (int i = 0; i < i_focus_points; i++) {
            for (int j = 0; j < i_cehuishu; j++) {
                S_gecehui.add(calculate_S.get(j)[i]);
            }
            Double delta_S = Collections.max(S_gecehui) - Collections.min(S_gecehui);//单位：米
            delta_S = delta_S * 1000;//单位：毫米
            if (delta_S > s_toler_gecehui) {
                error_set[0] = "V";
                error_set[1] = list_focus_1_round.get(i);
                list_error_fangxiang.add(error_set);
            }
        }
        return list_error_fangxiang;
    }

    public void next_station() {
        //将数据写入到.in2文件中
        List<String> list_in2_text = new ArrayList<String>();
        list_in2_text.add(station_name);
        Double[] mean_Hz = new Double[i_focus_points];
        Double[] mean_V = new Double[i_focus_points];
        Double[] mean_S = new Double[i_focus_points];

        for (int i = 0; i < i_focus_points; i++) {
            Double sum_Hz = 0.0;
            Double sum_V = 0.0;
            Double sum_S = 0.0;
            for (int j = 0; j < i_cehuishu; j++) {
                sum_Hz += calculate_Hz.get(j)[i];
                sum_V += calculate_Hz.get(j)[i];
                sum_S += calculate_S.get(j)[i];
            }
            mean_Hz[i] = sum_Hz / (double) i_cehuishu;
            mean_V[i] = sum_V / (double) i_cehuishu;
            mean_S[i] = sum_S / (double) i_cehuishu;

            String focus_point_name = list_focus_1_round.get(i);
            list_in2_text.add(focus_point_name + ",L," + my_func.rad2ang_show(mean_Hz[i]));
            list_in2_text.add(focus_point_name + ",S," + mean_S[i]);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file_in2, true));
            for (String item : list_in2_text) {
                bw.flush();
                bw.write(item + "\n");
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //清空file_ob_data
        try {
            file_ob_data.delete();
            file_ob_data.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始化
        editText_station_name.setText("");
        editText_station_hight.setText("");
        editText_focus_name.setText("");
        editText_focus_high.setText("");

        i_cehuishu = 1;//到下一个测站点去测，测回数要进行初始化
        i_focus_points = 0;
        list_focus_points.clear();
        list_focus_1_round.clear();
        list_Obdata.clear();
    }

    public void read_observe_tolerance() {
        //读取观测限差文件
        File Tolerance_Settings = new File(my_func.get_main_file_path(), "Tolerance Settings.ini");
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

        //1、水平角限差，单位：秒″
        hz_toler_zhaozhun = Integer.valueOf(List_tolerance.get(0));
        hz_toler_guiling = Integer.valueOf(List_tolerance.get(1));//水平角半测回归零差
        hz_toler_2C = Integer.valueOf(List_tolerance.get(2));//水平角一测回2C互差
        hz_toler_gecehui = Integer.valueOf(List_tolerance.get(3));//水平角同方向各测回互差

        //2、竖直角限差，单位：秒″
        v_toler_zhaozhun = Integer.valueOf(List_tolerance.get(4));
        v_toler_zhibiaocha = Integer.valueOf(List_tolerance.get(5));//竖直角一测回指标差互差
        v_toler_gecehui = Integer.valueOf(List_tolerance.get(6));//竖直角同方向各测回互差

        //3、距离限差，单位：毫米mm
        s_toler_zhaozhun = Integer.valueOf(List_tolerance.get(7));
        s_toler_gecehui = Integer.valueOf(List_tolerance.get(8));//距离同方向各测回互差
    }

    public void get_in2_and_write_total_torelance() {
        try {
            final File ProjectNow = my_func.get_ProjectNow();
            BufferedReader bf = new BufferedReader(new FileReader(ProjectNow));
            ProjectName_now = bf.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error：无法读取ProjectNow文件！");
        }
        file_in2 = new File(my_func.get_main_file_path() + "/"
                + ProjectName_now, ProjectName_now + ".in2");
        if (file_in2.exists()) {
            file_in2.delete();
            try {
                file_in2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //写入全站仪误差参数
        File file_total_station_tolerance = new File(my_func.get_main_file_path() + "/"
                + ProjectName_now, "total station tolerance.ini");
        File file_known_points = new File(my_func.get_main_file_path() + "/"
                + ProjectName_now, "known points.txt");
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(file_total_station_tolerance));
            BufferedReader br2 = new BufferedReader(new FileReader(file_known_points));

            BufferedWriter bw = new BufferedWriter(new FileWriter(file_in2, true));
            String readline = "";
            String write_text = "";
            while ((readline = br1.readLine()) != null) {
                write_text += readline + ",";
            }
            bw.flush();
            bw.write(write_text.substring(0, write_text.length() - 1) + "\n");
            bw.flush();

            while ((readline = br2.readLine()) != null) {
                bw.flush();
                bw.write(readline + "\n");
                bw.flush();
            }

            bw.close();
            br1.close();
            br2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}