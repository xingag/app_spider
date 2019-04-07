package com.xingag.qmxsp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import java.io.IOException;

public class Utils
{
    public static void startAPP(String sPackageName)
    {
        Context mContext = InstrumentationRegistry.getContext();

        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(sPackageName);  //启动app
        mContext.startActivity(myIntent);
    }

    public static void startAPP(UiDevice uiDevice, String sPackageName, String sLaunchActivity)
    {
        try
        {
            uiDevice.executeShellCommand("am start -n " + sPackageName + "/" + sLaunchActivity);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void closeAPP(UiDevice uiDevice, String sPackageName)
    {
        try
        {
            uiDevice.executeShellCommand("am force-stop " + sPackageName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
