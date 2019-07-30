package com.xingag.crack_wx;

import android.app.Application;

import com.xingag.util.RootUtils;

public class MyApplication extends Application
{
    public static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";

    public static MyApplication instance = null;


    public static MyApplication getApplication()
    {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        //实例
        instance = this;

        //获取Root权限
        getRootPermission();
    }

    private void getRootPermission()
    {
        RootUtils.execRootCmd("chmod 777 -R " + WX_ROOT_PATH);
    }
}
