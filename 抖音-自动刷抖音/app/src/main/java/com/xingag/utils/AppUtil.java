package com.xingag.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import static android.content.Context.CLIPBOARD_SERVICE;


public class AppUtil
{

    /***
     * 获取剪切板上的内容
     * @return
     */
    public static String getClipBoardContent(Context context)
    {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cd2 = cm.getPrimaryClip();
        String result = "";
        try
        {
            result = cd2.getItemAt(0).getText().toString();
        } catch (Exception e)
        {
            //pass
            Log.d("xag", "产生异常了。。。。");
        }
        return result;
    }

    /***
     * 获取当前应用的包名
     * @param mContext
     * @return
     */
    public static String getCurrentPacakgeName(Context mContext)
    {
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String mPackageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else
        {
            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        Log.d("xag","当前应用包名为:"+mPackageName);
        return mPackageName;
    }


}
