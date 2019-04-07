package com.xingag.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 执行命令
 */
public class CMDUtils
{

    private static final String TAG = "CMDUtils";

    public static class CMD_Result {
        public int resultCode;
        public String error;
        public String success;

        public CMD_Result(int resultCode, String error, String success) {
            this.resultCode = resultCode;
            this.error = error;
            this.success = success;
        }

    }

    /**
     * 执行命令
     *
     * @param command         命令
     * @param isShowCommand   是否显示执行的命令
     * @param isNeedResultMsg 是否反馈执行的结果
     * @retrun CMD_Result
     */
    public static CMD_Result runCMD(String command, boolean isShowCommand,
                                    boolean isNeedResultMsg) {
        if (isShowCommand)
            Log.i(TAG, "runCMD:" + command);
        CMD_Result cmdRsult = null;
        int result;
        Process process = null;
        PrintWriter pw = null;
        try {
            //我没有采用给APP添加系统签名的方式来获取系统权限，而是在代码中使用Runtime.getRuntime().exec("su")来获取root权限，但前提是你的手机已经root，且装有授权管理，允许应用来申请root权限。
            //当执行Runtime.getRuntime().exec("su");这行代码时，手机上会弹出一个对话框问你是否允许申请root，但只是执行这条命令有root权限而已，并不是整个程序都有root权限。这点需要特别注意。
            //部分设备中，system/xbin/su不允许第三方应用获取root权限
            //---------------------
            process = Runtime.getRuntime().exec("su"); //获取root权限
            pw = new PrintWriter(process.getOutputStream());
            pw.println(command);
            pw.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                StringBuilder successMsg = new StringBuilder();
                StringBuilder errorMsg = new StringBuilder();
                BufferedReader successResult = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                BufferedReader errorResult = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
                cmdRsult = new CMD_Result(result, errorMsg.toString(),
                        successMsg.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "run CMD:" + command + " failed");
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return cmdRsult;
    }

}
