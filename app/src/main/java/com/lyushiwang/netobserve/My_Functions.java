package com.lyushiwang.netobserve;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by win10 on 2017/4/18.
 */

public class My_Functions extends AppCompatActivity{
    public My_Functions(){

    }

    public void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        return;
    }
}
