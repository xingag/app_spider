package com.xingag.util;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class RootUtils
{
    /**
     * 执行linux指令
     *
     * @param paramString
     */
    public static void execRootCmd(String paramString)
    {
        try
        {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            localObject = localProcess.exitValue();
        } catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }
}
