package com.lyushiwang.netobserve;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by win10 on 2017/4/15.
 */

public class setting_common extends AppCompatActivity {
    private My_Functions my_functions=new My_Functions();

    private Button button_queding;
    private Button button_qingchu;
    private ImageButton imageButton_houtui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_common);

//        define_palettes();

//        do_click();
    }

    protected void define_palettes(){
        button_queding=(Button)findViewById(R.id.button_queding_common);
        button_qingchu=(Button)findViewById(R.id.button_qingchu_common);
        imageButton_houtui=(ImageButton)findViewById(R.id.imageButton_houtui);
    }

    protected void do_click() {
        button_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_functions.makeToast("成功");
            }
        });

        button_qingchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
