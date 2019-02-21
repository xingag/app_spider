#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: 东方头条.py 
@time: 2/8/19 16:56 
@description：东方头条新闻客户端
"""

__author__ = "xingag"

from airtest.core.api import *
from poco.exceptions import PocoNoSuchNodeException
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from utils.file_utils import *
from utils.string_utils import *
from queue import Queue
import datetime
from comments import generate_a_comment
from airtest.core.android import Android
from airtest_utils import *
from utils.device_utils import *
from utils.norm_utils import current_time

# 应用包名和启动Activity
package_name = 'com.songheng.eastnews'

activity = 'com.oa.eastfirst.activity.WelcomeActivity'

device_1 = Android('c54613d')

poco = AndroidUiautomationPoco(device_1, use_airtest_input=True, screenshot_each_action=False)

auto_setup(__file__)


# 收益来源
# 0.顶部的时长领取金币
# 1.任务：包含签到、
# 1.收益
# 2.阅读新闻
# 3.评论


class DongFangTouTiao(object):
    """
    东方头条
    """

    def __init__(self):
        # 保留最新的5条新闻标题
        self.news_titles = []

        # 视频播放时间
        self.video_play_time = 30

        # 间隔获取标题栏的金币
        self.interval_time = 5

        # 跳过的页数
        self.skip_page = 0

    def run(self):

        # 0.打开应用
        home()
        stop_app(package_name)
        start_my_app(package_name, activity)

        # 1.预加载
        self.__pre_and_skip_ads()

        # 2.获取顶部的金币
        self.get_top_title_coin()

        # 3.任务
        # self.norm_task()

        # 4.查看推荐的新闻
        self.__skip_same_pages()

        while True:
            self.watch_news_recommend()
        
            print('查看一页完成，继续查看下一页的新闻。')
        
            # 顶部金币领取
            self.get_top_title_coin()
        
            # 滑动下一页的新闻
            poco.swipe([0.5, 0.8], [0.5, 0.3], duration=1)

        # 5.看视频
        # self.__video()

        # 6.看小视频
        # self.mini_video()

    def __sign_in(self):
        """
        任务-签到
        :return:
        """
        pass

    def __lottery(self):
        """
        任务：大转盘抽奖
        :return:
        """
        pass

    def watch_news_recommend(self):
        """
        查看新闻
        :return:
        """

        # 1.推荐的所有新闻元素
        lv_elements = poco('com.songheng.eastnews:id/g_').children()

        if not lv_elements.exists():
            print('新闻列表不存在')
            return

        # 下面的循环经常会报错：PocoNoSuchNodeException
        # 遍历每一条新闻
        for news_element in lv_elements:

            # 1.查看要闻
            self.__read_key_news()

            # 2.新闻标题
            news_title = news_element.offspring('com.songheng.eastnews:id/pb')

            # 作者
            author_element = news_element.offspring('com.songheng.eastnews:id/a4f')

            # 3.注意：必须保证元素加载完全
            # 下面会报错：hrpc.exceptions.RpcRemoteException: java.lang.IndexOutOfBoundsException
            try:
                if not news_title.exists() or not author_element.exists():
                    print("【标题】元素加载不完全" if not news_title.exists() else "【发布者】元素加载不完全")
                    continue
            except Exception as e:
                print("******注意注意注意！exist()方法报错******")
                print("判断下面两个东西是否存在")
                print(e)
                self.__back_to_list()
                print('回到首页')
                return

            # 4.过滤广告
            # 到这里标识此条新闻：是一条有效的新闻【包含广告】
            # 注意：部分广告【包含点击标题就自动下载，左下角显示广告字眼等】要过滤掉
            # 场景一：
            if news_element.attr('name') == 'android.widget.FrameLayout':
                print('广告！这是一个FrameLayout广告，标题是:%s' % news_title.get_text())
                continue

            # 常见二：点击标题直接下载其他应用
            ads_tips_element = news_element.offspring(name='com.songheng.eastnews:id/a4f', text='广告通')
            if ads_tips_element.exists():
                print('广告！这是一个【广点通】广告，标题是:%s' % news_title.get_text())
                continue

            # 常见三：有效角标识是广告的图标【奇虎广告】
            ads_tips_element2 = news_element.offspring('com.songheng.eastnews:id/q5')
            if ads_tips_element2.exists():
                print('广告！广告标题是：%s' % news_title.get_text())
                continue

            # 已经查看过了，过滤掉
            if news_title.get_text() in self.news_titles:
                print('已经看过了，不看了！')
                continue

            # ==========================================================================
            # 5.查看新闻
            # 下面是一条有效的新闻
            # 新闻类型
            # 文字0、视频1、图片2
            news_type = self.get_news_type(news_element)

            if 5 == len(self.news_titles):
                self.news_titles.pop()
            self.news_titles.insert(0, news_title.get_text())

            print('==' * 30)
            print('当前时间:%s' % current_time())
            print('准备点击刷新闻，这条新闻的标题是:%s' % news_title.get_text())

            # 以上还在主界面
            # 如果是正常的新闻就点击进去
            news_title.click()

            # 等待新闻元素都加载完全
            sleep(2)

            print('这条新闻类型:%d' % news_type)

            print('已阅读新闻包含：')
            for temp_title in self.news_titles:
                print(temp_title)

            # 评论拿金币和发表按钮
            comments_with_coins = poco('com.songheng.eastnews:id/m9')
            submit_btn_element = poco("com.songheng.eastnews:id/m6").offspring('com.songheng.eastnews:id/vw')
            # 记录时长的标识
            # 不存在就直接返回
            red_coin_element = poco('com.songheng.eastnews:id/aq8')
            if not red_coin_element.exists():
                print('当前新闻没有红包，返回！')
                self.__back_keyevent()
                continue

            if comments_with_coins.exists() and comments_with_coins.get_text() == '评论拿金币':

                # 输入评论拿金币
                # comments_with_coins.click()
                # comments_edittext_element = poco('com.songheng.eastnews:id/vt')

                # 注意：部分新闻不容许评论，原因你懂的
                # if comments_with_coins.attr('editalbe'):
                #     comments_edittext_element.set_text(generate_a_comment())
                #
                #     print('按钮文字内容:%d' % submit_btn_element.get_text())
                # 发表按钮
                # submit_btn_element.click()
                # print('点击之后休眠5秒钟')
                # sleep(5)
                # 退回到当前新闻页面
                #     self.__back()
                # else:
                #     print('注意！！！当前新闻不容许评论！！！')
                #     self.__back()

                oldtime = datetime.datetime.now()

                # 文字
                if news_type == 0:
                    while True:
                        print("循环-滑动查看内容")
                        self.__swipe(True if random.randint(0, 1) == 0 else False)

                        # 如果发现有【点击查看全文】按钮，点击查看全文
                        see_all_article_element = poco('点击查看全文')
                        if see_all_article_element.exists():
                            print('点击展开全文内容...')
                            see_all_article_element.focus('center').click()

                            # 注意：有的时候点击展开全文，会点击到图片，需要规避一下
                            while poco('com.songheng.eastnews:id/lz').exists():
                                print('不小心点到图片了，返回到新闻详情页面')
                                self.__back_keyevent()

                        newtime = datetime.datetime.now()
                        interval_time = (newtime - oldtime).seconds
                        if interval_time >= 30:
                            print('阅读30秒新闻完成')
                            break
                        self.__read_key_news()
                # 视频
                elif news_type == 1:
                    while True:
                        print("循环-滑动查看视频")
                        newtime = datetime.datetime.now()
                        interval_time = (newtime - oldtime).seconds
                        if interval_time >= 30:
                            print('观看30秒视频完成')
                            break
                        self.__read_key_news()
            else:
                print('这是一篇没有金币的文章！')

            print('==' * 30)

            self.__back_to_list()

    def __video(self):
        """
        查看视频
        :return:
        """
        poco('com.songheng.eastnews:id/ko').click()

        while True:
            # 视频列表
            poco('com.songheng.eastnews:id/a0z').wait_for_appearance()
            sleep(2)

            self.__read_key_news()

            video_elements = poco('com.songheng.eastnews:id/a0z').children()

            print('video items是否存在：')
            print(video_elements.exists())

            # 遍历视频
            # 注意：视频播放完全可以提前返回
            for video_element in video_elements:
                # 1.标题元素
                video_title_element = video_element.offspring('com.songheng.eastnews:id/a3q')
                # 播放按钮
                video_play_element = video_element.offspring('com.songheng.eastnews:id/nj')

                # 2.必须保证【视频标题】和【播放按钮】都可见
                if not video_title_element.exists() or not video_play_element.exists():
                    continue

                # 3.标题
                video_title = video_element.offspring('com.songheng.eastnews:id/a3q').get_text()

                print('当前视频的标题是:%s,播放当前视频' % video_title)

                # 点击播放视频
                video_play_element.focus("center").click()

                # 4.播放视频
                self.play_video()

                print('播放下一个视频')

                self.__back_keyevent()

            # 滑动到下一页的视频
            poco.swipe([0.5, 0.8], [0.5, 0.3], duration=0.2)

    def mini_video(self):
        """
        查看小视频
        :return:
        """
        poco('com.songheng.eastnews:id/kr').click()

        # 加载出列表元素,点击第一项进入
        poco('com.songheng.eastnews:id/a0p').child('com.songheng.eastnews:id/g_').wait_for_appearance(60)
        poco('com.songheng.eastnews:id/a0p').child('com.songheng.eastnews:id/g_').children()[0].click()

        while True:
            sleep(30)
            # 向左滑动
            poco.swipe([0.9, 0.5], [0.1, 0.5], duration=0.2)

    def __swipe(self, up_or_down):
        """
        滑动单条新闻
        :param up_or_down: true：往上滑动；false：往下滑动【慢慢滑动】
        :return:
        """
        if up_or_down:
            poco.swipe([0.5, 0.6], [0.5, 0.4], duration=0.5)
        else:
            poco.swipe([0.5, 0.4], [0.5, 0.6], duration=0.5)

    def get_news_type(self, news_element):
        """
        获取新闻的类型【文字0、视频1、图片2】
        :param news_element:
        :return:
        """
        # 默认是文字新闻
        type = 0
        video_element = poco('com.songheng.eastfirst.business.video.view.widget.ijkplayer.h')
        if video_element.exists():
            type = 1

        return type

    def __wait_for_element_exists(self, elements):
        """
        一直等待元素出现
        :param elements: 元素列表
        :return:
        """
        try:
            while True:
                # 元素是否存在
                element_exists = True

                # 元素列表
                for element in elements:
                    if not element.exists():
                        element_exists = False
                        break
                    else:
                        continue

                if element_exists:
                    break
                else:
                    print('元素暂时找不到，继续等待')
                    continue
        except PocoNoSuchNodeException as e:
            print('找不到这个元素异常')

    def __remove_disturb(self):
        # 退出对话框元素
        exit_dialog_tips_element = poco('com.songheng.eastnews:id/xm')
        if exit_dialog_tips_element.exists():
            self.__back_keyevent()

    def __pre_and_skip_ads(self):
        """
        预加载和跳过广告
        :return:
        """
        # 1.广告页面元素的出现
        # 两种样式：跳过、跳过广告*秒

        try:
            poco('com.songheng.eastnews:id/aoy').wait_for_appearance(10)
        except Exception as e:
            print('等待广告元素异常')
            print(e)

        ads_element = poco(name='com.songheng.eastnews:id/aoy', textMatches='^跳过广告.*$')
        ads_element1 = poco(name='android.widget.TextView', text='跳过')
        # ads_element = poco(name='com.songheng.eastnews:id/aoy')

        # Splash 图片加载完成的时候打印UI树

        # print_ui_tree(poco)
        # write_ui_tree(poco)

        # 跳过广告(0s)
        if ads_element.exists():
            print('跳过广告1!!!')
            ads_element.click()
        if ads_element1.exists():
            print('跳过广告2!!!')
            ads_element1.click()

        # 2.等到到达主页面
        poco('com.songheng.eastnews:id/g_').wait_for_appearance(120)

    def __read_key_news(self):
        """
        处理【要闻】对话框，需要阅读
        :return:
        """
        # 对于弹出来的要闻对话框，需要处理
        key_news_element = poco(name='com.songheng.eastnews:id/x2', text='立即查看')
        if key_news_element.exists():
            print('要闻推送！需要看一下')
            key_news_element.click()

            # TODO  需不需要另外停留
            sleep(3)
            self.__back_keyevent()

    def norm_task(self):
        """
        普通任务领取金币【包含：签到、大转盘】
        :return:
        """
        self.__sign_in()
        self.__lottery()

    def play_video(self):
        """
        播放一个视频
        :return:
        """

        # 开始时间
        start_time = datetime.datetime.now()

        while True:
            # 视频播放结束或者超过30秒
            scale_element = poco('com.songheng.eastnews:id/oy')

            if scale_element.exists():
                print('视频播放完了，结束播放。')
                break

                # 结束时间
            end_time = datetime.datetime.now()

            # 时间间隔
            interval_time = (end_time - start_time).seconds

            if interval_time > 30:
                print('播放超过30秒，结束播放。')
                break

    def get_top_title_coin(self):
        """
        顶部金币领取
        仅仅在新闻首页的时候才可以领取
        :return:
        """
        get_coin_element = poco(name='com.songheng.eastnews:id/arq', text="领取")

        if get_coin_element.exists():
            print('顶部有金币可以领取！')
            get_coin_element.click()

            print('领完金币后可以关闭对话框！')
            # 关掉对话框
            self.__back_keyevent()
        else:
            print('顶部没有金币或者不在首页')

    def __skip_same_pages(self):
        """
        往下滑动【跳过】几页
        :param num:
        :return:
        """
        current_page = 0
        while current_page < self.skip_page:
            poco.swipe([0.5, 0.8], [0.5, 0.3], duration=1)
            current_page += 1

        print('跳过结束，继续获取金币')

    def __back_keyevent(self):
        """
        返回的时候可能会出现关键要闻
        :return:
        """
        self.__read_key_news()
        back_keyevent()

    def __back_to_list(self):
        """
        回退到首页
        :return:
        """
        print('准备回到首页')
        while not poco('com.songheng.eastnews:id/g_').exists():
            print('回退一次')
            self.__back_keyevent()


if __name__ == "__main__":
    dftt = DongFangTouTiao()
    dftt.run()
