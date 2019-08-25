package com.xingag.xianyu.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.xingag.base.BaseService;
import com.xingag.util.AppUtils;
import com.xingag.xianyu.Constants;
import com.xingag.xianyu.Ids;

import java.util.ArrayList;
import java.util.List;

public class XianYuService extends BaseService
{
    private static final String TAG = "xag";

    //默认的TopActivity
    private String topActivity = Constants.class_name_xianyu;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        //事件类型
        int eventType = event.getEventType();
        //获取包名
        String packageName = event.getPackageName().toString();
        //类名
        String className = event.getClassName().toString();

//        Log.e("xxx", packageName + className);

        //只筛选下面三个事件，其他事件过滤掉
        if (eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED && eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        {
            Log.e("xxx","eventType:"+eventType);
            return;
        }

        switch (eventType)
        {
            //状态栏变化
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.e(TAG, "TYPE_NOTIFICATION_STATE_CHANGED," + packageName + className);
                //跳到聊天界面
                Log.e(TAG, "通知栏消息，点击跳入");
                handleNotificationEventMet(event);
                break;
            //窗口切换的时候回调
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.e(TAG, "TYPE_WINDOW_STATE_CHANGED," + packageName + className);

                //设置TopActivity
                topActivity = className;
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (null == event.getSource())
                {
                    Log.e(TAG, "TYPE_WINDOW_STATE_CHANGED:事件过滤掉");
                    return;
                }

                //判断className
                //主页
                if (TextUtils.equals(className, Constants.class_name_xianyu) && AppUtils.judgeIsMainPage(getRootInActiveWindow()))
                {
                    handleMainMsg(event);
                }
                //聊天界面
                else if (TextUtils.equals(className, Constants.class_name_chat) && AppUtils.judgeIsChatPage(event.getSource()))
                {
                    handleChatMet(event);
                } else
                {
                    //其他界面，不进行处理
                    Log.e(TAG, "界面变更在其他界面：" + className + "，不处理！！！");
                }
                break;
            //窗口内容变化
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                //注意：此处的className(com.taobao.idlefishandroid.widget.TextView)和TopActivity不一样
                Log.e(TAG, "TYPE_WINDOW_CONTENT_CHANGED," + packageName + className + ",topActivity:" + topActivity);

                //过滤掉一下特殊的事件
                if (null == event.getSource())
                {
                    Log.e(TAG, "TYPE_WINDOW_CONTENT_CHANGED:事件过滤掉");
                    return;
                }

                if (TextUtils.equals(topActivity, Constants.class_name_xianyu) && AppUtils.judgeIsMainPage(getRootInActiveWindow()))
                {
                    handleMainMsg(event);
                } else if (TextUtils.equals(topActivity, Constants.class_name_chat) && AppUtils.judgeIsChatPage(event.getSource()))
                {
                    handleChatMet(event);
                } else
                {
                    Log.e(TAG, "当前窗口内容变化的类名:" + className + "，不处理");
                }
                break;
        }
    }

    /***
     * 处理聊天消息
     * @param event
     */
    private void handleChatMet(AccessibilityEvent event)
    {
        String eventClassName = event.getClassName().toString();

        Log.e(TAG, "聊天界面！处理聊天界面消息");

        //滑动到最底部
        performScrollBackward();

        //聊天列表
        AccessibilityNodeInfo chat_lv = findViewByID(Ids.id_chat_listview);

        int chat_count = chat_lv.getChildCount();

        //注意：包含自己发送的消息、收到的消息、公共提示消息，比如：一直未回复？试试语音/视频通话
        Log.e(TAG, "一共有:" + chat_count + "条聊天记录");

        //所有聊天记录
//        List<AccessibilityNodeInfo> chat_nodes = new ArrayList<>();

        //系统聊天信息，可以忽略
        List<String> chat_msgs_common = new ArrayList<>();
        //收到的信息
        List<String> chat_msgs_from = new ArrayList<>();
        //发出去的信息
        List<String> chat_msgs_to = new ArrayList<>();


        for (int i = 0; i < chat_count; i++)
        {

            AccessibilityNodeInfo tempChatAccessibilityNodeInfo = chat_lv.getChild(i);
            //注意：通过判断是否有头像元素、昵称元素来判断是哪种聊天记录

            //获取头像元素【左】【发送者】
            AccessibilityNodeInfo headPortraitAccessibilityNodeInfoLeft = findViewByID(tempChatAccessibilityNodeInfo, Ids.id_chat_head_portrait_left);

            //获取头像元素【右】【接受者】
            AccessibilityNodeInfo headPortraitAccessibilityNodeInfoRight = findViewByID(tempChatAccessibilityNodeInfo, Ids.id_chat_head_portrait_right);

            //获取文字内容
            AccessibilityNodeInfo tempTextAccessibilityNodeInfo = findViewByID(tempChatAccessibilityNodeInfo, Ids.id_chat_text);

            if (null == tempTextAccessibilityNodeInfo || null == tempTextAccessibilityNodeInfo.getText())
            {
                Log.e(TAG, "索引" + i + "，聊天内容为空");
                continue;
            }
            String chatText = tempTextAccessibilityNodeInfo.getText().toString();

            Log.e(TAG, "聊天内容为:" + chatText);

            if (null != headPortraitAccessibilityNodeInfoLeft)
            {
                chat_msgs_from.add(0, chatText);
            } else if (null != headPortraitAccessibilityNodeInfoRight)
            {
                chat_msgs_to.add(0, chatText);
            } else
            {
                chat_msgs_common.add(0, chatText);
            }
        }

        Log.e(TAG, "*************************************");
        Log.e(TAG, "聊天内容如下【接受】:");
        Log.e(TAG, chat_msgs_from.toString());
        Log.e(TAG, "聊天内容如下【发送】:");
        Log.e(TAG, chat_msgs_to.toString());
        Log.e(TAG, "聊天内容如下【公共】:");
        Log.e(TAG, chat_msgs_common.toString());
        Log.e(TAG, "*************************************");

        // 初次聊天就发送默认的信息
        if (chat_msgs_from.size() == 1 && chat_msgs_to.size() == 0)
        {
            //输入之后，返回，退出输入框
            Log.e(TAG, "第一次收到信息，回复默认准备的内容");
            reply_content(event, Constants.reply_first);

        } else if (chat_msgs_from.size() > 0)
        {
            //第一条文本内容
            String first_msg = chat_msgs_from.get(0);
            if ("11".equals(first_msg))
            {
                reply_content(event, Constants.reply_11);
            } else if ("22".equals(first_msg))
            {
                reply_content(event, Constants.reply_22);
            } else
            {
                reply_content(event, Constants.reply_other);
            }

        } else
        {
            Log.e(TAG, "对方没有应答，不处理");
        }
        //返回到聊天界面
        performBackClick();
    }

    /***
     * 回复消息
     * @param event
     */
    private void reply_content(AccessibilityEvent event, String content)
    {
        //元素：输入框
        AccessibilityNodeInfo chat_edit = findViewByID(Ids.id_edittext);

        //把文本输入进去
        inputText(chat_edit, content);

        //元素：发送按钮
        AccessibilityNodeInfo chat_send = findViewByID(Ids.id_sendtext);

        Log.e(TAG,"准备回复的内容是:"+content);

        try
        {
            Thread.sleep(5000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //发送按钮
        performViewClick(chat_send);
    }

    /***
     * 处理通知栏消息
     * @param event
     */
    private void handleNotificationEventMet(AccessibilityEvent event)
    {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification)
        {
            Notification notification = (Notification) event.getParcelableData();
            if (notification == null)
            {
                return;
            }
            PendingIntent pendingIntent = notification.contentIntent;
            if (pendingIntent == null)
            {
                return;
            }
            try
            {
                //注意：通知栏的文字消息没有参考意义
                //跳转到聊天信息界面
                Log.e("xag", "准备跳转到聊天界面");
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e)
            {
                e.printStackTrace();
            }
        }

    }

    /***
     * 处理主界面的消息
     */
    private void handleMainMsg(AccessibilityEvent event)
    {
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Log.e(TAG, "主界面！处理主界面消息");

        Constants.HOME_TAB currentTab = AppUtils.getCurrentTab(getRootInActiveWindow().findAccessibilityNodeInfosByViewId(Ids.id_tabs).get(0));

        //选中在消息Tab，处理聊天页面
        if (Constants.HOME_TAB.TAB_XIAO_XI == currentTab)
        {
            Log.e(TAG, "当前Tab：消息Tab");
            //判断新消息在哪个位置，然后点击进入
            AccessibilityNodeInfo main_listview_node = findViewByID(Ids.id_main_conversation_listview);

            //未读消息Node
            AccessibilityNodeInfo unread_tag_node = AppUtils.getUnreadMsgNode(main_listview_node);

            if (null != unread_tag_node)
            {
                Log.e(TAG, "点击进入会话");
                //点击进入消息列表
                performViewClick(unread_tag_node);

                //处理聊天信息
                handleChatMet(event);
            } else
            {
                Log.e(TAG, "列表中没有未读消息");
            }
        }
    }

    @Override
    public void onInterrupt()
    {
    }


}
