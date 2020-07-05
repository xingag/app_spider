package com.xingag.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

/**
 * 执行命令
 */

public class RootCmd
{
    private static final String TAG = "RootCmd";

    /**
     * 执行控制台命令，参数为命令行字符串方式，申请Root控制权限
     */
    public static boolean RootCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
      /*  BufferedReader buf=null;
        InputStream is = null;*/
        try
        {
            process = Runtime.getRuntime().exec("su");//执行这一句，superuser.apk就会弹出授权对话框
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e)
        {
            Log.e(TAG, "获取root权限失败： " + e.getMessage());
            return false;
        }
        Log.d(TAG, "获取root权限成功");
        return true;
    }


    public static void execCommand(String command) throws IOException
    {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        //proc = Runtime.getRuntime().exec("su");
        try
        {
            if (proc.waitFor() != 0)
            {
                System.err.println("exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null)
            {
                stringBuffer.append(line + "-");
            }
            //打印出执行cmd命令后返回的数据
            System.out.println("****" + stringBuffer.toString());

        } catch (InterruptedException e)
        {
            System.err.println(e);
        }
    }

    /**
     * 执行命令
     *
     * @param command
     * @return
     */
    public static String android_command(String command)
    {
        Process process = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        String temp;
        try
        {
            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((temp = reader.readLine()) != null)
            {
                buffer.append(temp);
            }
            return buffer.toString();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}

