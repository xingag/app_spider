package com.xingag.xianyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/***
 * 配置文件
 */

public class SettingConfig
{
    private SharedPreferences sharedPreferences;

    @SuppressLint("StaticFieldLeak")
    private static SettingConfig settingConfig;

    private Context context = null;

    //配置文件名
    private final String SETTING_NAME = "setting";


    public static SettingConfig getInstance()
    {
        if (settingConfig == null)
        {
            synchronized (SettingConfig.class)
            {
                if (settingConfig == null)
                {
                    settingConfig = new SettingConfig();
                }
            }
        }
        return settingConfig;
    }


    public void init(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SETTING_NAME, Context.MODE_PRIVATE);
    }
}
