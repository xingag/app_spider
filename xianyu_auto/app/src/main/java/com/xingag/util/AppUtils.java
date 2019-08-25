package com.xingag.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.xingag.xianyu.Constants;
import com.xingag.xianyu.Ids;

import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AppUtils
{
    /***
     * 判断是否是聊天界面
     * @param node
     * @return
     */
    public static boolean judgeIsChatPage(AccessibilityNodeInfo node)
    {
        boolean result = false;
        Log.e("xag", "node==null:" + (node == null));
        List<AccessibilityNodeInfo> tag1_node = node.findAccessibilityNodeInfosByViewId(Ids.id_chat_page_tag1);
        List<AccessibilityNodeInfo> tag2_node = node.findAccessibilityNodeInfosByViewId(Ids.id_chat_page_tag2);
        List<AccessibilityNodeInfo> tag3_node = node.findAccessibilityNodeInfosByViewId(Ids.id_chat_page_tag3);
        List<AccessibilityNodeInfo> tag4_node = node.findAccessibilityNodeInfosByViewId(Ids.id_chat_page_tag4);
        List<AccessibilityNodeInfo> tag5_node = node.findAccessibilityNodeInfosByViewId(Ids.id_chat_page_tag5);

        if (null != tag1_node && null != tag2_node && null != tag3_node && null != tag4_node && null != tag5_node
                && tag1_node.size() > 0 && tag2_node.size() > 0 && tag3_node.size() > 0 && tag4_node.size() > 0 && tag5_node.size() > 0)
        {
            result = true;
        }
        return result;
    }


    /***
     * 判断是否是主界面
     * @param node
     * @return
     */
    public static boolean judgeIsMainPage(AccessibilityNodeInfo node)
    {
        boolean result = false;
        List<AccessibilityNodeInfo> tag1_node = node.findAccessibilityNodeInfosByText("闲鱼");
        List<AccessibilityNodeInfo> tag2_node = node.findAccessibilityNodeInfosByText("鱼塘");
        List<AccessibilityNodeInfo> tag3_node = node.findAccessibilityNodeInfosByText("发布");
        List<AccessibilityNodeInfo> tag4_node = node.findAccessibilityNodeInfosByText("消息");
        List<AccessibilityNodeInfo> tag5_node = node.findAccessibilityNodeInfosByText("我的");

        //根据5个Tab文本来判断
        if (null != tag1_node && null != tag2_node && null != tag3_node && null != tag4_node && null != tag5_node
                && 1 <= tag1_node.size() && 1 <= tag2_node.size() && 1 <= tag3_node.size() && 1 <= tag4_node.size() && 1 <= tag5_node.size())
        {
            result = true;
        }

        String temp = result ? "当前页面：主页" : "当前页面：非主页";

        Log.e("xag", temp);

        return result;
    }


    /***
     * 获取消息列表中有未读的消息Node
     * @param node
     * @return
     */
    public static AccessibilityNodeInfo getUnreadMsgNode(AccessibilityNodeInfo node)
    {
        AccessibilityNodeInfo unread_node = null;

        if (node == null || node.getChildCount() <= 1)
        {
            Log.e("xag", "未读消息判断，node==null");
            return unread_node;
        }

        Log.e("xag", "未读消息判断，子类个数:" + node.getChildCount());

        //过滤ListView的头部，包含：通知消息、互动消息、活动消息、鱼塘消息
        for (int i = 1; i < node.getChildCount(); i++)
        {

            AccessibilityNodeInfo temp_node = node.getChild(i);
            List<AccessibilityNodeInfo> unread_nodes = temp_node.findAccessibilityNodeInfosByViewId(Ids.id_main_unread_tag);
            if (null == unread_nodes || unread_nodes.size() == 0)
            {
                Log.e("xag", "未读消息判断，索引:" + i + ",unread_nodes为空");
            } else
            {
                unread_node = unread_nodes.get(0);
                break;
            }
        }

        Log.e("xag", "未读消息判断，unread_node==null:" + (unread_node == null));

        return unread_node;

    }

    /***
     * 获取当前Tab
     * @param node
     */
    public static Constants.HOME_TAB getCurrentTab(AccessibilityNodeInfo node)
    {
        int count = node.getChildCount();

//        Log.e("xag", "子tab有:" + count + "个");

        int select_index = 0;

        Constants.HOME_TAB result = Constants.HOME_TAB.TAB_XIAO_XI;

        for (int i = 0; i < count; i++)
        {
            AccessibilityNodeInfo tempNode = node.getChild(i);


            //获取ContentDesc
            CharSequence content_desc = tempNode.getContentDescription();

            if (TextUtils.isEmpty(content_desc))
            {

//                Log.e("xag", "index:" + i + "，content为空");
                continue;
            } else
            {
//                Log.e("xag", "index:" + i + "，content为:" + content_desc.toString());
            }

            //选中的索引
            //0:闲鱼首页、1：鱼塘、3：消息、4：我的
            if (!content_desc.toString().contains("未选中状态"))
            {
                select_index = i;
                break;
            }
        }
        switch (select_index)
        {
            case 0:
                result = Constants.HOME_TAB.TAB_XIAN_YU;
                Log.e("xag", "首页");
                break;
            case 1:
                result = Constants.HOME_TAB.TAB_YU_TANG;
                Log.e("xag", "鱼塘");
                break;
            case 3:
                result = Constants.HOME_TAB.TAB_XIAO_XI;
                Log.e("xag", "消息");
                break;
            case 4:
                result = Constants.HOME_TAB.TAB_MINE;
                Log.e("xag", "我的");
                break;
            default:
                break;
        }

        return result;

    }


}
