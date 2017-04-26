package com.lyushiwang.netobserve;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private Button button_finish;
    private ListView listview;
    private ImageButton imageButton_houtui;

    private List<String> list_known_points = new ArrayList<String>();

    private boolean[] checkItems;

    Map<String, Object> map;
    private List<Map<String, Object>> list_listview = new ArrayList<Map<String, Object>>();
    private MyAdapter listview_adapter;

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
        button_finish = (Button) findViewById(R.id.button_finish);
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


                List<String> list_text = get_and_check_text();
                if (list_text != null) {
                    map = new HashMap<String, Object>();
                    map.put("Name", list_text.get(0).toString());
                    map.put("X_coor", list_text.get(1).toString());
                    map.put("Y_coor", list_text.get(2).toString());
                    map.put("Z_coor", list_text.get(3).toString());
                    list_listview.add(map);
                    listview_adapter = new MyAdapter(observe_known_point.this, list_listview);
                    listview.setAdapter(listview_adapter);
                    listview_adapter.notifyDataSetChanged();

                    String name = list_text.get(0).toString();
                    String X_coor = list_text.get(1).toString();
                    String Y_coor = list_text.get(2).toString();
                    String Z_coor = list_text.get(3).toString();
                    list_known_points.add(name + "," + X_coor + "," + Y_coor + "," + Z_coor);
                    makeToast("已添加！");
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
                final String[] string_known_points = input_known_points(list_known_points);
                checkItems = new boolean[list_known_points.size()];
                for (int i=0;i<list_known_points.size();i++){
                    checkItems[i]=false;
                }

                AlertDialog.Builder AD_delete_point = new AlertDialog.Builder(observe_known_point.this);
                AD_delete_point.setTitle("请选择需要删除的已知点");
                AD_delete_point.setMultiChoiceItems(string_known_points, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkItems[which] = isChecked;
                    }
                });
                AD_delete_point.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> list_delete_points = new ArrayList<String>();
                        for (int i = 0; i < checkItems.length; i++) {
                            if (checkItems[i]) {
                                list_known_points.remove(string_known_points[i]);
                                list_listview.remove(i);//有问题，删除一个之后，每一项的序号会改变！！
                            }
                        }
                        listview_adapter = new MyAdapter(observe_known_point.this, list_listview);
                        listview.setAdapter(listview_adapter);
                        listview_adapter.notifyDataSetChanged();
                    }
                });
                AD_delete_point.setNegativeButton("取消",null);
                AD_delete_point.show();
            }
        });

        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ProjectName_now = get_ProjectNow_name();

                File file_known_points = new File(my_functions.get_main_file_path() + "/" + ProjectName_now, "known points.txt");
                file_known_points.delete();
                try {
                    file_known_points.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file_known_points, true));
                    for (String item : list_known_points) {
                        bw.flush();
                        bw.write(item + "\n");
                        bw.flush();
                    }
                    bw.close();
                    makeToast("保存成功！");
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Error：无法为known points.txt文件创建BufferedWriter!");
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

    public String[] input_known_points(List<String> list_points) {
        String[] string_known_points = new String[list_points.size()];

        int i = 0;
        for (String item : list_points) {
            string_known_points[i] = item;
            i += 1;
        }
        return string_known_points;
    }

    public String get_ProjectNow_name() {
        String ProjectName_now = new String();
        File file_ProjectNow = my_functions.get_ProjectNow();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file_ProjectNow));
            ProjectName_now = bf.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProjectName_now;
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