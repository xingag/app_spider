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

    //自动发货内容
    private static final String AUTO_DELIVER_CONTENT = "auto_reply_content";
    //自动发货状态，默认关闭
    private static final String AUTO_DELIVER_STATUS = "auto_reply_status";


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

    /***
     * 设置自动化发货的内容
     * @param content
     */
    public void setAutoDeliverContent(String content)
    {
        sharedPreferences.edit().putString(AUTO_DELIVER_CONTENT, content).apply();
    }

    /***
     * 获取自动发货的内容
     * @return
     */
    public String getAutoDeliverContent()
    {
        return sharedPreferences.getString(AUTO_DELIVER_CONTENT, "");
    }

    //自动发货机器人，默认是关闭
    public Boolean getAutoDeliverStatus()
    {
        return sharedPreferences.getBoolean(AUTO_DELIVER_STATUS, false);
    }


    //设置自动发货机器人的状态
    public void setAutoDeliverStatus(Boolean status)
    {
        sharedPreferences.edit().putBoolean(AUTO_DELIVER_STATUS, status).apply();
    }
}
