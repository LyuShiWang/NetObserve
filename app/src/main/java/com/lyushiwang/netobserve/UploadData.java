package com.lyushiwang.netobserve;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by win10 on 2017/5/12.
 */

public class UploadData extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView_project;
    private List<String> list_project;
    private ArrayAdapter<String> project_adapter;
    private String ProjectName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_data);

        init();
    }

    protected void init(){
        listView_project=(ListView)findViewById(R.id.listview_project);

        project_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, list_project);
        listView_project.setAdapter(project_adapter);
        listView_project.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder AD_upload=new AlertDialog.Builder(UploadData.this);
        AD_upload.setTitle("确认框").setMessage("是否确定上传该工程数据？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("取消",null).create().show();
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
