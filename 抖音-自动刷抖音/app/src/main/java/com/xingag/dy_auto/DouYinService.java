package com.xingag.dy_auto;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.xingag.bean.VideoItem;
import com.xingag.bean.VideoNewItem;
import com.xingag.inter.WaitInterface;
import com.xingag.network.Header;
import com.xingag.utils.AppUtil;
import com.xingag.utils.RootCmd;
import com.xingag.utils.StringUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DouYinService extends BaseService
{
    //主页面Activity
    private static final String PAGE_MAIN = "com.ss.android.ugc.aweme.main.MainActivity";

    //分享按钮id
    private static final String ID_SHARE = "com.ss.android.ugc.aweme:id/f4j";

    //复制按钮
    private static final String ID_COPY_LINK = "com.ss.android.ugc.aweme:id/f3o";
    private static final String TEXT_COPY_LINK = "复制链接";

    //分享Tag
    private static final String ID_SHARE_TAG = "com.ss.android.ugc.aweme:id/f51";
    private static final String TEXT_SHARE_TAG = "分享到";

    private String url = "";

    //当前视频时长
    private int videoDuration = 10;

    private boolean isBusy = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        String className = event.getClassName().toString();
        String packName = event.getPackageName().toString();

        if (TextUtils.equals(PAGE_MAIN, className))
        {
            //循环操作
            //判断是否在当前应用
            while (TextUtils.equals(packName, getResources().getString(R.string.package_name_dy)))
            {
                Log.d("xag","开始滑动");

                //当前应用非抖音，就退出处理
                if (isBusy)
                {
                    continue;
                }

                //点击分享
                AccessibilityNodeInfo shareElement = findViewByID(ID_SHARE);
                performViewClick(shareElement);

                //找到复制链接按钮，然后执行点击操作
                boolean result = copyLinkMet();

                if (result)
                {
                    Log.d("xag", "复制视频地址成功！");
                    //从剪切板中拿到视频的分享链接，并进行一次过滤
                    url = StringUtil.findUrlByStr(AppUtil.getClipBoardContent(getApplicationContext()));

                    Log.d("xag", "剪切板内容:" + AppUtil.getClipBoardContent(getApplicationContext()));
                    Log.d("xag", "过滤后的视频地址是:" + url);

                    //获取这个视频的时长（异步）
                    parsePage();
                }
            }
        }
    }


    /***
     * 复制视频地址到剪切板
     */
    private boolean copyLinkMet()
    {
        boolean result = false;

        //等待弹出分享弹框出现
        waitForAppearSync(ID_SHARE_TAG, TEXT_SHARE_TAG, 10);

        //滑动到复制按钮可见
        while (true)
        {
            if (null == findViewByIDAndText(ID_SHARE_TAG, TEXT_SHARE_TAG))
            {

                break;
            }

            AccessibilityNodeInfo copyElement = findViewByIDAndText(ID_COPY_LINK, TEXT_COPY_LINK);
            if (null == copyElement)
            {
                try
                {
                    Runtime.getRuntime().exec("adb shell input swipe 900 1600 300 1600");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                performViewClick(copyElement);
                result = true;
                break;
            }

        }
        return result;
    }


    private void parsePage()
    {
        isBusy = true;
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                try
                {
                    //获取重定向的url
                    url = Jsoup.connect(url)
                            .followRedirects(true)
                            .execute().url().toExternalForm();

                    //从重定向后的地址，解析出视频id，即：item_ids
                    //https://www.iesdouyin.com/share/video/6837860764536065295/?region=CN&mid=6811099886067796740&u_code=0&titleType=title&utm_source=copy_link&utm_campaign=client_share&utm_medium=android&app=aweme
                    Log.e("xag", "重定向地址：\n" + url);
                    String item_id = StringUtil.getSubUtil(url, "video/(.*?)/\\?region").get(0);
                    Log.d("xag", "item_id:" + item_id);

                    String new_url = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + item_id;
                    Connection connection = Jsoup.connect(new_url).ignoreContentType(true);
                    Connection data = connection.headers(Header.generateHeader());
                    String result = data.get().body().html();

                    Log.d("xag", "result:" + result);

                    //解析数据
                    VideoNewItem item = JSON.parseObject(result, VideoNewItem.class);

                    //获取视频时长（毫秒）
                    int videoDuration = item.getItem_list().get(0).getDuration();

                    Log.d("xag", "视频时长:" + videoDuration + ",开始等待。。。");

                    //倒计时，等待时间播放完成
                    Thread.sleep(videoDuration);
                    Log.d("xag", "等待完成，准备滑到下一个视频");

                    try
                    {
                        Runtime.getRuntime().exec("adb shell input swipe 600 1200 600 600");
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    isBusy = false;
                } catch (Exception e)
                {
                    isBusy = false;
                    Log.d("xag", "产生异常,异常信息:" + e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public void onInterrupt()
    {
        Log.d("xag", "服务被打断,清空消息");
    }
}
