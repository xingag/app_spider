package com.xingag.qmxsp;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.text.TextUtils;
import android.util.Log;

import com.xingag.bean.VideoItem;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/***
 * 【全名小视频】客户端
 */

@RunWith(AndroidJUnit4.class)
public class GetDeal
{
    private final String TAG = getClass().getName();

    private static UiDevice mDevice;

    private String mPackageName = "com.baidu.minivideo";
    private String mLaunchActivityName = "app.activity.splash.SplashActivity";

    //等待时长
    private long timeout = 2000L;

    //屏幕宽、高
    private int device_width = 0;
    private int device_height = 0;

    private int step = 50;

    @BeforeClass
    public static void init()
    {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Before
    public void start()
    {
        //获取屏幕高度、宽度
        device_width = mDevice.getDisplayWidth();
        device_height = mDevice.getDisplayHeight();

        Log.d("xag", "start");
        Utils.closeAPP(mDevice, mPackageName);
        Utils.startAPP(mPackageName);
        waitForAMoment();
    }

    /**
     * 等待固定的时间
     */
    private void waitForAMoment()
    {
        mDevice.waitForWindowUpdate(mPackageName, timeout);
    }

    @After
    public void end()
    {
        Utils.closeAPP(mDevice, mPackageName);
        waitForAMoment();
    }

    @Test
    public void start_qmxsp() throws InterruptedException
    {
        Log.d("xag", "进入全名小视频App中,等待2秒钟");

        //等待首页元素加载完全
        boolean result = mDevice.wait(Until.hasObject(By.res("com.baidu.minivideo:id/fragment_index_recycler")), 10 * 1000);
//        Log.d("xag", "首页元素加载" + (result ? "完成" : "超时"));

        if (result)
        {
            //主页列表元素
            UiObject2 rv = mDevice.findObject(By.res("com.baidu.minivideo:id/fragment_index_recycler"));

            //列表元素下面的子元素列表
            if (rv.getChildCount() > 0)
            {
                //获取第一项元素，点击进入
                UiObject2 first_video_element = rv.getChildren().get(0);

                first_video_element.click();

                //进入播放页面
                waitForAMoment();


                while (true)
                {
                    //获取当前视频的信息
                    //等待播放30秒+一个随机数
                    int wait_time = NumUtils.geneRandom(10, 40);
                    Log.d("xag", "播放时间:" + wait_time + "s");

                    //当前视频信息
                    VideoItem item = get_current_video_info(wait_time);
                    Log.d("xag", item.toString());

                    //作者为空，代表是一条广告
                    if (TextUtils.isEmpty(item.getAuthor()))
                    {
                        Log.d("xag", "这是一条广告，滑到下一个视频~");
                    } else
                    {
                        Thread.sleep(wait_time * 1000);
                        Log.d("xag", "等待结束,滑到下一个视频~");
                    }


                    play_next_video();
                }

            } else
            {
                Log.d("xag", "没有视频");
            }
        } else
        {
            Log.d("xag", "超时了~");
        }


    }

    /***
     * 获取当前视频信息
     * @param play_time 视频播放时间
     */
    private VideoItem get_current_video_info(int play_time)
    {
        UiObject2 author_element = mDevice.findObject(By.res("com.baidu.minivideo:id/detail_author_name"));
        UiObject2 content_element = mDevice.findObject(By.res("com.baidu.minivideo:id/detail_title"));

        String author = (null == author_element) ? "" : author_element.getText();
        String content = (null == content_element) ? "" : content_element.getText();

        VideoItem item = new VideoItem(play_time, author, content);

        return item;
    }

    /***
     * 下一个视频
     */
    private void play_next_video()
    {
        //手机按下的坐标和抬手的坐标
        int top = NumUtils.geneRandom(20, 150);
        int bottom = device_height - top;

        int top_x = NumUtils.geneRandomWithOffset(device_width / 2, 10);
        int bottom_x = NumUtils.geneRandomWithOffset(device_width / 2, 10);

        Log.d("xag", "滑动底部坐标：" + bottom_x + "/" + bottom + ";顶部坐标：" + top_x + "/" + top);

        mDevice.swipe(bottom_x, bottom, top_x, top, step);

        mDevice.waitForIdle(timeout);
    }

    /***
     * 上一个视频
     */
    private void play_last_video()
    {
        //手机按下的坐标和抬手的坐标
        int top = NumUtils.geneRandom(20, 150);
        int bottom = device_height - top;

        int top_x = NumUtils.geneRandomWithOffset(device_width / 2, 10);
        int bottom_x = NumUtils.geneRandomWithOffset(device_width / 2, 10);

        Log.d("xag", "滑动底部坐标：" + bottom_x + "/" + bottom + ";顶部坐标：" + top_x + "/" + top);

        mDevice.swipe(top_x, top, bottom_x, bottom, step);

        mDevice.waitForIdle(timeout);
    }

}
