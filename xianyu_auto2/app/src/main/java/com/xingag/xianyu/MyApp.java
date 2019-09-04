package com.xingag.xianyu;

import android.app.Application;

import com.xingag.base.BaseService;

public class MyApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        initService();
    }

    /***
     * 初始化服务
     */
    private void initService()
    {
        //初始化辅助功能基类
        BaseService.getInstance().init(getApplicationContext());

        //初始化配置文件
        SettingConfig.getInstance().init(getApplicationContext());

    }
}
