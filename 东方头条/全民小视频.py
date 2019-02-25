#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: 全民小视频.py 
@time: 2/9/19 12:21 
@description：全名小视频
"""
__author__ = "xingag"

from airtest.core.api import *
from poco.exceptions import PocoNoSuchNodeException
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from airtest.core.android import Android
from utils.file_utils import *
from utils.string_utils import *
from queue import Queue
import datetime
from comments import generate_a_comment
from airtest_utils import *
from utils.device_utils import *
from utils.norm_utils import current_time
from airtest.core.android.constant import *

package_name = 'com.baidu.minivideo'
activity_name = 'app.activity.splash.SplashActivity'

# ================================================================================================
device_1 = Android('c54613d')
poco = AndroidUiautomationPoco(device_1, use_airtest_input=True, screenshot_each_action=False)


# ==================================================================================================

auto_setup(__file__)


# 任务
# 1.签到
# 2.查看视频30个，每个视频15秒
# 3.分享视频3个
# 4.看【推送】5次

# 其他：认证


class QuMingMiniVideo(object):

    def __init__(self):
        self.watch_time = 18

        # 1.看小视频次数【15秒以上】
        # 防止网络不好，看的长度不够
        self.watch_mini_video_num = 35

        # 2.分享视频次数3次
        self.share_num = 4

    def __pre(self):
        """
        初始化
        :return:
        """
        print('初始化中...')
        poco('com.baidu.minivideo:id/fragment_index_recycler').wait_for_appearance(60)
        print('初始化完成')

    def run(self):
        # 关闭当前应用
        stop_app(package_name)

        # 0.打开应用
        # 方式一：使用POCO
        # poco(text='全民小视频').click()

        # 方式二：使用adb打开应用
        start_app(package_name, activity_name)

        # 1.初始化
        self.__pre()

        # 2.抢红包
        # self.__get_red_package()

        # 2.签到
        # self.__sign_in()

        # print('签到任务完成，休眠2秒钟，马上开始看视频')
        # sleep(2)

        # 3.点击【发现】Tab 下第一个视频
        poco('com.baidu.minivideo:id/fragment_index_recycler').children()[1].click()

        # 3.1 等待视频加载完全
        poco('com.baidu.minivideo:id/video_view').wait_for_appearance(10)

        # 4.观看视频并分享几个视频
        self.see_mini_video()

        # 5.退出应用
        back_keyevent()
        home_keyevent()

        print('完成任务！')

    def see_mini_video(self):
        """
        查看小视频
        :return:
        """
        sleep(self.watch_time)

        # 查看次数
        watch_num = 1

        # 分享次数
        share_num = 1

        while True:
            # 滑动观看下一个视频
            poco.swipe([0.5, 0.99], [0.5, 0.01], duration=0.2)
            print('查看一个视频，已看%d个' % watch_num)

            print('share_num：%d' % share_num)

            # 保证分享次数够
            if share_num <= self.share_num:
                print('视频分享一下')
                # 分享按钮
                share_btn = poco('com.baidu.minivideo:id/detail_bottom_share_icon')
                if share_btn.exists():
                    # 开始分享
                    # 注意：可能调用不起来分享对话框
                    while not poco('com.baidu.minivideo:id/share_weixin').exists():
                        print('点击一次分享')
                        share_btn.click()

                    print('分享对话框出现了！')

                    # 等分享对话框出现后，直接分享到微信
                    poco('com.baidu.minivideo:id/share_weixin').wait(5).click()
                    poco(text='星安果').wait(20).click()
                    # 点击分享按钮
                    poco('com.tencent.mm:id/ayb').wait(5).click()

                    # 回到视频播放界面
                    poco('com.tencent.mm:id/aya').wait(5).click()

                    share_num += 1

            time.sleep(self.watch_time)
            watch_num += 1

            if watch_num >= self.watch_mini_video_num:
                print('观看任务完成！已经观看视频30个！')
                break

    def __sign_in(self):
        """
        应用签到，每天一次;并开宝箱
        :return:
        """

        poco('com.baidu.minivideo:id/view_index_top_inner_container').child(
            'android.widget.FrameLayout').child('android.widget.ImageView').wait_for_appearance()
        print('去挣钱图标出现了')
        to_get_coin = poco('com.baidu.minivideo:id/view_index_top_inner_container').child(
            'android.widget.FrameLayout').child('android.widget.ImageView')

        # 到任务中心，就会自动签到成功
        to_get_coin.click()

        # TODO... 关闭签到对话框

        # 点击一下开宝箱
        poco("当前钻石: ").wait_for_appearance()

        print('界面加载成功')
        sleep(5)

        self.__open_treasure_box()

        # 返回
        back_keyevent()

    def __open_treasure_box(self):
        """
        开宝箱
        :return:
        """
        # 由于定位比较困难，这里使用坐标去定位元素，模拟点击操作
        # poco(boundsInParent="[0.06666666666666667, 0.059375]").click()
        pass

    def __get_red_package(self):
        """
        抢红包
        :return:
        """
        # 下拉一次
        # poco.swipe([0.5, 0.3], [0.5, 0.8], duration=0.2)

        # 等待入口元素加载出来
        poco('com.baidu.minivideo:id/banner_view_pager').wait_for_appearance()

        # 点击
        poco('com.baidu.minivideo:id/banner_view_pager').click()

        # 等待页面元素加载完成
        poco('分享').wait_for_appearance()

        # 获取UI树  http://wanandroid.com/tools/bejson
        sleep(5)
        # write_ui_tree(poco)

        while True:
            print('%s\n抢红包' % current_time())
            touch(Template(r"tpl1550544728116.png", record_pos=(0.011, 0.59), resolution=(1080, 1920)))
            sleep(5)


if __name__ == '__main__':
    qm_video = QuMingMiniVideo()
    qm_video.run()
