package com.xingag.dy_auto;

import android.app.Application;
import android.util.Log;

import java.io.IOException;

public class BaseApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        //尝试获取root权限
        try
        {
            Runtime.getRuntime().exec("su");
        } catch (IOException e)
        {
            Log.d("xag","尝试获取root权限，失败");
            e.printStackTrace();
        }
    }
}
