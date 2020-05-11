package com.xingag.readmsg;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xingag.bean.ShowFloatBean;
import com.xingag.utils.SpUtil;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MsgService extends AccessibilityService
{
    //公众号文章页面
    public static final String CLASS_NAME_PAGE_ARTICLE = "com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewTooLMpUI";

    //Web元素
    private AccessibilityNodeInfo webNode = null;

    //当前页面
    public static String currentClassName = "";

    //内容
    private ArrayList<String> contents = new ArrayList<>();

    /**
     * 连接服务成功后回调该方法
     */
    @Override
    protected void onServiceConnected()
    {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"com.tencent.mm"};
        serviceInfo.notificationTimeout = 100;

        //保证能够获取到Web元素
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);

        Toast.makeText(MsgService.this, "连接服务成功",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {

        //当前页面
        String tempClassName = event.getClassName().toString();
        Log.d("xag", "tempClassName:" + tempClassName);

        //过滤
        if (!TextUtils.equals(tempClassName, "android.widget.FrameLayout")
                && !TextUtils.equals(tempClassName, "android.widget.LinearLayout")
                && !TextUtils.equals(tempClassName, "android.widget.RelativeLayout"))
        {
            currentClassName = event.getClassName().toString();
        }

        //如果是微信公众号文章页面
        if (TextUtils.equals(currentClassName, CLASS_NAME_PAGE_ARTICLE))
        {
            //等待页面加载
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            //显示或关闭弹出框
            EventBus.getDefault().post(new ShowFloatBean(true));
            Log.d("xag", "文章页面，准备展示。。。");
            webNode = null;

            new Thread()
            {
                @Override
                public void run()
                {
                    //查找WebView元素
                    findWebViewNode(getRootInActiveWindow());

                    //从WebView中查找标题
                    if (webNode != null)
                    {
                        //清空上一次的记录
                        contents.clear();

                        //获取文章内容
                        getAllContents(webNode);

                        Log.d("xag", "要播放的内容如下：");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < contents.size(); i++)
                        {
                            sb.append(contents.get(i)).append("；；；");
                            Log.d("xag", contents.get(i));
                        }
                        Log.d("xag", "*******************获取完成*********************");

                        //存储
                        SpUtil.clear(BaseApplication.getInstance());
                        SpUtil.put("contents", sb.toString());
                        Log.d("xag", "添加内容完毕，可以播放了...");
                    } else
                    {
                        Log.d("xag", "webview为空");
                    }
                }
            }.start();

        }
    }

    /***
     * 获取所有的文本内容
     * @param webNode
     * @return
     */
    private void getAllContents(AccessibilityNodeInfo webNode)
    {
        for (int i = 0; i < webNode.getChildCount(); i++)
        {
            AccessibilityNodeInfo tempNode = webNode.getChild(i);
            String id = tempNode.getViewIdResourceName();
            //过滤
            if (TextUtils.equals("meta_content", id))
            {
                continue;
            }
            String tempContent = tempNode.getText().toString().trim();
            //加入内容
            if (!TextUtils.isEmpty(tempContent))
            {
                contents.add(tempContent);
            }
            //循环遍历
            //判断是否有子节点
            if (tempNode.getChildCount() > 0)
            {
                for (int j = 0; j < tempNode.getChildCount(); j++)
                {
                    getAllContents(tempNode.getChild(j));
                }
            }
        }
    }


    /**
     * @param id id
     * @return 文本内容
     */
    public String findViewContentByID(AccessibilityNodeInfo container, String id)
    {
        String result = "";
        if (null == container)
        {
            return result;
        }
        List<AccessibilityNodeInfo> nodeInfoList = container.findAccessibilityNodeInfosByViewId(id);

        if (!nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    Log.d("xag", "内容为:" + nodeInfo.getText().toString());
//                    return nodeInfo.getText().toString();
//                    return null;
                }
            }
        }
        return null;
    }

    /***
     * 查找WebView元素
     */
    public void findWebViewNode(AccessibilityNodeInfo rootNode)
    {
        if (rootNode == null)
        {
            return;
        }
        for (int i = 0; i < rootNode.getChildCount(); i++)
        {
            AccessibilityNodeInfo current = rootNode.getChild(i);
            if ("android.webkit.WebView".contentEquals(current.getClassName()))
            {
                webNode = current;
                return;
            }
            if (current.getChildCount() != 0)
            {
                findWebViewNode(current);
            }
        }
    }


    /**
     * 模拟返回操作
     */
    public void performBackClick()
    {
        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }


    /**
     * 功能：模拟用户点击操作
     *
     * @param text
     */
    private void findAndPerformActions(AccessibilityEvent event, String text)
    {
        if (event.getSource() != null)
        {
            List<AccessibilityNodeInfo> action_nodes = event.getSource()
                    .findAccessibilityNodeInfosByText(text);

            if (action_nodes != null && !action_nodes.isEmpty())
            {
                AccessibilityNodeInfo node = null;
                for (int i = 0; i < action_nodes.size(); i++)
                {
                    node = action_nodes.get(i);
                    // 执行按钮点击行为
                    if (node.isEnabled())
                    {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }
    }


    /***
     * 元素是否存在
     * @param event
     * @param text
     * @return
     */
    private boolean findElementWithText(AccessibilityEvent event, String text)
    {
        AccessibilityNodeInfo node = null;

        if (event.getSource() != null)
        {
            List<AccessibilityNodeInfo> action_nodes = event.getSource()
                    .findAccessibilityNodeInfosByText(text);

            if (action_nodes != null && !action_nodes.isEmpty())
            {
                for (int i = 0; i < action_nodes.size(); i++)
                {
                    if (action_nodes.get(i) != null)
                    {
                        node = action_nodes.get(i);
                        break;
                    }

                }
            }
        }
        return node != null;
    }

    /***
     * 是否包含文本
     * @param event
     * @param text
     * @return
     */
    private boolean findElementContainsText(AccessibilityEvent event, String text)
    {
        AccessibilityNodeInfo node = null;

        if (event.getSource() != null)
        {
            List<AccessibilityNodeInfo> action_nodes = event.getSource()
                    .findAccessibilityNodeInfosByText(text);

            if (action_nodes != null && !action_nodes.isEmpty())
            {
                for (int i = 0; i < action_nodes.size(); i++)
                {
                    if (action_nodes.get(i) != null)
                    {
                        node = action_nodes.get(i);
                        break;
                    }

                }
            }
        }
        return node != null;
    }


    @Override
    public void onInterrupt()
    {
        Log.d("xag", "无障碍服务被打断");
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("xag", "无障碍服务销毁");
    }
}
