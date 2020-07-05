package com.xingag.dy_auto;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;


import com.xingag.inter.WaitInterface;
import com.xingag.utils.ScreenUtil;
import com.xingag.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/***
 * 无障碍服务的基类
 */


public class BaseService extends AccessibilityService
{

    @SuppressLint("StaticFieldLeak")
    private static BaseService mInstance = null;

    private Context mContext = null;

    //无障碍服务管理器
    private AccessibilityManager mAccessibilityManager = null;


    public static BaseService getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new BaseService();
        }
        return mInstance;
    }

    public void init(Context context)
    {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {

    }

    @Override
    public void onInterrupt()
    {

    }

    /**
     * 检查当前无障碍服务是否开启
     *
     * @param serviceName 服务名称
     * @return 服务是否启用
     */
    @SuppressLint("LongLogTag")
    public boolean checkAccessibilityEnabled(String serviceName)
    {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices)
        {
            Log.e("checkAccessibilityEnabled", info.getId());
            if (info.getId().equals(serviceName))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * 查找对应ID的View  Level>=18
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String id)
    {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }


    /***
     * 等待元素出现（同步）
     * @param id  元素id
     * @param content  文本内容
     * @param timeout  最长时长，单位：秒
     */
    public void waitForAppearSync(final String id, final String content, int timeout)
    {
        int foo = 0;
        while (foo < timeout)
        {
            if ((!TextUtils.isEmpty(id) && !TextUtils.isEmpty(content) && null != findViewByIDAndText(id, content)) || (!TextUtils.isEmpty(id) && null != findViewByID(id)))
            {
                break;
            } else
            {
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                foo++;
            }
        }
    }


    /***
     * 等待元素出现（异步）
     * @param id  元素id
     * @param content  文本内容
     * @param timeout  最长时长，单位：秒
     */
    public void waitForAppear(final String id, final String content, int timeout, final WaitInterface waitInterface)
    {
        CountDownTimer countDownTimer = new CountDownTimer(timeout * 1000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                Log.d("xag", "millisUntilFinished:" + millisUntilFinished);
                //id、text/id
                if ((!TextUtils.isEmpty(id) && !TextUtils.isEmpty(content) && null != findViewByIDAndText(id, content)) || (!TextUtils.isEmpty(id) && null != findViewByID(id)))
                {
                    cancel();
                    waitInterface.findSuccess();
                } else
                {
                    Log.d("xag", "查找不到");
                }
            }

            @Override
            public void onFinish()
            {
                cancel();
                waitInterface.findError();
            }
        };
        countDownTimer.start();
    }


    /***
     * 通过ID和Text查找控件
     * @param id
     * @param content
     * @return
     */
    public AccessibilityNodeInfo findViewByIDAndText(String id, String content)
    {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null && TextUtils.equals(nodeInfo.getText(), content))
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }


    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(AccessibilityNodeInfo parentNode, String id)
    {
        if (parentNode == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = parentNode.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text)
    {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }


    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text)
    {
        //Level>=21 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
        //Level>=18  4.3.1
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }


    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo)
    {
        if (nodeInfo == null)
        {
            return;
        }
        while (nodeInfo != null)
        {
            if (nodeInfo.isClickable())
            {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }


    }


    private void findEveryViewNode(AccessibilityNodeInfo node, String content)
    {
        if (null != node && node.getChildCount() > 0)
        {
            for (int i = 0; i < node.getChildCount(); i++)
            {
                AccessibilityNodeInfo child = node.getChild(i);
                // 有时 child 为空
                if (child == null)
                {
                    continue;
                }
                String className = child.getClassName().toString();
                CharSequence text_raw = child.getText();

                if ("android.view.View".equals(className) && !TextUtils.isEmpty(text_raw))
                {
                    boolean isClickable = child.isClickable();

                    //isClickable：可点击的按钮，按钮内容是继续
                    if (isClickable && TextUtils.equals(content, text_raw.toString()))
                    {
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                }
                // 递归调用
                findEveryViewNode(child, content);
            }
        }
    }

}
