package com.lyushiwang.netobserve;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class project_manage extends AppCompatActivity {
    private My_Functions my_functions = new My_Functions();
    private Context mContext;
    private Button xinjiangongcheng,dakaigongcheng,guanbigongcheng,lingcungongcheng,shanchugongcheng,zuijinshiyonggongcheng;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_manage);

        mContext = getApplicationContext();

        final File ProjectList = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ProjectList.list");
        if (!ProjectList.exists()) {
            try {
                ProjectList.createNewFile();//用于储存所有的工程名
            } catch (Exception e) {
                e.printStackTrace();
                makeToast("Error：无法创建工程列表文件！");
            }
        }
        define_palettes();

        do_click();
    }

    protected void define_palettes() {
        xinjiangongcheng = (Button) findViewById(R.id.gongcheng_button1);
        dakaigongcheng = (Button) findViewById(R.id.gongcheng_button2);
        guanbigongcheng = (Button) findViewById(R.id.gongcheng_button3);
        lingcungongcheng = (Button) findViewById(R.id.gongcheng_button4);
        shanchugongcheng = (Button) findViewById(R.id.gongcheng_button5);
        zuijinshiyonggongcheng = (Button) findViewById(R.id.gongcheng_button6);
        imageButton_houtui = (ImageButton) findViewById(R.id.imageButton_houtui);
    }

    protected void do_click() {
        xinjiangongcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"点击新建成功！",Toast.LENGTH_LONG).show();
                Intent intent_manage2newProject = new Intent();
                intent_manage2newProject.setClass(project_manage.this, create_new_project.class);
                startActivity(intent_manage2newProject);
            }
        });

        dakaigongcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File ProjectNow = new File(my_functions.get_main_file_path(), "ProjectNow.name");
                if (!ProjectNow.exists()) {
                    try {
                        ProjectNow.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        makeToast("Error：无法创建ProjectNow文件！");
                    }
                }//该文件用于储存当前工程名

                //导入已有的所有工程的名字
                final File ProjectList = my_functions.get_ProjectList();
                final String[] List = Input_All_Project(ProjectList);//获取所有工程文件名的函数

                //弹出窗口
                if (List.length != 0) {
                    AlertDialog.Builder AD_dakai = new AlertDialog.Builder(project_manage.this);
                    AD_dakai.setTitle("请选择要打开的工程");
                    AD_dakai.setItems(List, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            makeToast("工程 " + List[which] + " 已打开！");
                            final String ProjcetName_now = List[which];
                            try {
                                ProjectNow.delete();
                                ProjectNow.createNewFile();
                                FileOutputStream outputStream = new FileOutputStream(ProjectNow);
                                outputStream.flush();
                                outputStream.write(ProjcetName_now.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                makeToast("Error：无法为ProjectNow文件创建写入流！");
                            }
                        }
                    });
                    AD_dakai.show();
                } else {
                    makeToast("暂无工程，请创建！");
                }
            }
        });

        guanbigongcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File ProjectNow = my_functions.get_ProjectNow();
                String ProjectName_now = my_functions.read_ProjectNow_Name(ProjectNow);//获取当前文件名的函数
                if (ProjectName_now != null) {
                    if (ProjectNow.exists()) {
                        ProjectNow.delete();
                        try {
                            ProjectNow.createNewFile();
                            AlertDialog.Builder AD_close = new AlertDialog.Builder(project_manage.this);
                            AD_close.setMessage("工程" + ProjectName_now + "已关闭！");
                            AD_close.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            makeToast("无法在删除ProjectNow文件后重新创建！");
                        }
                    } else {
                        makeToast("Error：ProjectNow文件不存在！");
                    }
                } else {
                    makeToast("还未打开工程！");
                }
            }
        });

        lingcungongcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        shanchugongcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File ProjectList = my_functions.get_ProjectList();
                final String[] List = Input_All_Project(ProjectList);//获取所有工程文件名的函数

                if (List.length != 0) {
                    AlertDialog.Builder AD_delete = new AlertDialog.Builder(project_manage.this);
                    AD_delete.setTitle("请选择要删除的工程");
                    AD_delete.setSingleChoiceItems(List, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String ProjectName = List[which];
                            //要删除的工程必须先关闭
                            String Name_now = my_functions.read_ProjectNow_Name(my_functions.get_ProjectNow());
                            if (Name_now == ProjectName) {
                                Delete_Project(my_functions.get_ProjectList(), ProjectName, which);
                                makeToast("工程" + ProjectName + "已删除！");
                            } else {
                                makeToast("该工程还未关闭！无法删除！");
                            }
                        }
                    });
                    AD_delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AD_delete.show();
                } else {
                    makeToast("还没有工程！请先创建！");
                }
            }
        });

        zuijinshiyonggongcheng.setOnClickListener(new View.OnClickListener() {
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

    protected void Delete_Project(File ProjectList, String ProjectName, int which) {
        List<String> stringList = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(ProjectList));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                stringList.add(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProjectList.delete();
        try {
            ProjectList.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stringList.remove(which);
        try {
            //true意思是，是否以append的方式写入
            BufferedWriter bw = new BufferedWriter(new FileWriter(ProjectList, true));
            bw.flush();
            for (String name : stringList) {
                bw.write(name + "\n");
            }
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error：无法将当前的工程名写入ProjectList文件中！");
        }
        //从工程列表文件中删除它

        File Project_dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + File.separator + ProjectName);
        if (Project_dir.exists()) {
            File[] Project_file_group = Project_dir.listFiles();//返回其下属的文件组
            for (File file : Project_file_group) {
                file.delete();
            }
            Project_dir.delete();
        }//删除该工程所在的文件夹及其下属文件
    }

    public String[] Input_All_Project(File ProjectList) {
        List<String> stringList = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(ProjectList));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                stringList.add(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] List = new String[stringList.size()];
        int i = 0;
        for (String name : stringList) {
            List[i] = name;
            i = i + 1;
        }
        return List;
    }

    public void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
