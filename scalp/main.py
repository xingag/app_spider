#!/usr/bin/env python
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: main.py
@time: 2019-09-05 11:42
@description：TODO
"""

__author__ = "xingag"

from airtest.core.api import *
from poco.exceptions import PocoNoSuchNodeException
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from utils.file_utils import *
from utils.string_utils import *
from utils.device_utils import *
from utils.airtest_utils import *
from utils.element_utils import *
from utils.image_utils import *
from queue import Queue
import datetime
from Ids import *
import time


# -----------------手淘App--------------------------------
package_name = 'com.taobao.taobao'
activity = 'com.taobao.tao.welcome.Welcome'


# -------------------------------------------------

# -----------------知乎App--------------------------------
# package_name = 'com.zhihu.android'
# activity = '.app.ui.activity.LauncherActivity'
# -------------------------------------------------


class TaoBao(object):

    def __init__(self, key, *args):
        self.poco = AndroidUiautomationPoco(screenshot_each_action=False)

        auto_setup(__file__)

        # 搜索关键字
        self.key = key

        # 浏览商品详情的时间，默认时长为20s
        if len(args) == 1 or len(args) == 2:
            self.browser_detail_time = args[0]
        else:
            self.browser_detail_time = 20

        # 浏览评论的时长，默认时长为5s
        self.browser_evalute_time = args[1] if len(args) == 2 else 5

        # 待匹配的商品主图
        self.main_img_path = ''

    def run(self):
        # 1、准备工作,打开淘宝客户端
        self.__pre()

        # 2、根据关键字，搜索商品
        self.__search_good_by_key()

        # 3、剪切图片，拿到商品主图
        self.main_img_path = crop_main_img('./333.jpeg')

        # 4、滑动，从列表中匹配商品
        self.__search_good_from_list()

        # 5、收藏商品
        self.__collect_good()

        # 6、浏览商品
        self.__browser_good_detail()

        # 7、查看评论
        self.__browser_good_evalute()

        # 8、购买
        self.__buy_good()

        # 9、获取订单编号
        order_no = self.__get_order_no()

        # 10、截取订单截图页面
        screenshot_pic_result = self.get_order_pic()

        print(f'购买完成！\n订单号为：{order_no}\n订单截图路径:{screenshot_pic_result}')

    def __collect_good(self):
        """
        收藏商品
        :return:
        """
        # 滑动一次，使顶部Tab显示出来
        self.__swipe(True)
        self.__swipe(True)

        # 判断商品是否收藏
        fav_element = self.poco('com.taobao.taobao:id/fav_title')

        if fav_element.get_text() == '收藏':
            print('开始收藏...')
            fav_element.parent().children()[0].click()
            back_keyevent()
        else:
            print('商品已经收藏')

    def __pre(self):
        """
        准备工作
        :return:
        """
        # 删除缓存文件
        remove_cache('./part.jpg', './screenshot.png', './uidump.xml')

        home()
        stop_app(package_name)
        start_my_app(package_name, activity)

    def __search_good_by_key(self):
        """
        通过关键字搜索商品
        :return:
        """
        self.poco(id_page_main_button_search).wait(5).click()
        # perform_view_id_click(poco, id_page_main_button_search)
        perform_view_input(self.poco, id_page_search_edittext_search, self.key)

        # 点击搜索
        self.poco(id_page_search_button_search).wait_for_appearance()
        while self.poco(id_page_search_button_search).exists():
            print('点击一次搜索')
            perform_view_id_click(self.poco, id_page_search_button_search)

        # 等待列表加载出来
        self.poco(id_page_goods_rv).wait_for_appearance()

    def __search_good_from_list(self):
        """
        从列表中匹配商品
        :return:
        """
        # 循环的图片查找
        while True:
            try:
                pos = loop_find(Template(self.main_img_path), timeout=10, threshold=0.95)
            except TargetNotFoundError:
                print('滑动一页')
                # self.poco.swipe([0.5, 0.8], [0.5, 0.4], duration=0.2)
                self.__swipe(True)
            else:
                print('找到了')
                # poco.click([100.0 / 1920, 100.0 / 1080])

                print(pos)

                # 屏幕宽度和高度
                screen_size = self.poco.get_screen_size()
                print(screen_size)

                # 点击的坐标点（宽、高）
                # (0.22407407407407406, 0.8550925925925926)
                position_click = (pos[0] / screen_size[0], pos[1] / screen_size[1])
                print(position_click)
                self.poco.click(position_click)
                break

    def __swipe(self, up_or_down):
        """
        滑动单条新闻
        :param up_or_down: true：往上滑动；false：往下滑动【慢慢滑动】
        :return:
        """
        if up_or_down:
            self.poco.swipe([0.5, 0.8], [0.5, 0.4], duration=0.2)
        else:
            self.poco.swipe([0.5, 0.4], [0.5, 0.8], duration=0.2)

    def __browser_good_detail(self):
        """
        浏览商品
        :return:
        """
        # 切换到详情Tab
        self.poco('com.taobao.taobao:id/taodetail_nav_bar_tab_text', text='详情').click()

        # 滑动时长为: self.browser_detail_time
        browser_start = datetime.datetime.now()
        browser_end = browser_start

        while (browser_end - browser_start).seconds < self.browser_detail_time:
            # 休眠一会
            time.sleep(random.randint(2, 5))

            # 滑动一次
            self.__swipe(True)

            # 结束时间
            browser_end = datetime.datetime.now()

        print('详情页面查看完毕')

    def __browser_good_evalute(self):
        """
        查看评论
        :return:
        """
        print('查看评论')
        # 切换到详情Tab
        self.poco('com.taobao.taobao:id/taodetail_nav_bar_tab_text', text='评价').click()

        # 查看全部评论
        self.poco('com.taobao.taobao:id/mainpage').offspring(text='查看全部').click()

        browser_start = datetime.datetime.now()
        browser_end = browser_start

        while (browser_end - browser_start).seconds < self.browser_evalute_time:
            # 休眠一会
            time.sleep(random.randint(2, 5))

            # 滑动一次
            self.__swipe(True)

            # 结束时间
            browser_end = datetime.datetime.now()

        print('评价页面查看完毕')

    def __buy_good(self):
        """
        购买商品
        :return:
        """
        # 立即购买
        self.poco('com.taobao.taobao:id/detail_main_sys_button', text='立即购买').click()

        # 选择商品属性
        sleep(10)

        # 确定购买
        self.poco('com.taobao.taobao:id/confirm_text', text='确定').parent().click()

        # 提交订单
        self.poco(text='提交订单').click()

        # 手动输入密码或者指纹
        sleep(10)

    def __get_order_no(self):
        """
        获取订单编号
        :return:
        """
        global copy_element
        while True:
            # 由于手机屏幕的限制，【复制】按钮第一页不一定能显示出来

            try:
                copy_element = self.poco(text='复制')
            except Exception as e:
                print('没有找到元素，往下滑动一页')
                self.__swipe(True)

            break

        copy_element.click()

        # 从剪切板拿到数据
        result = exec_cmd('adb shell am broadcast -a clipper.get')[1]

        # 匹配出订单号
        result = re.findall(r'data="(.*)"', result)

        order_no = ''

        if result and len(result) > 0:
            order_no = result[0]

        print(order_no)

        return order_no

    def get_order_pic(self):
        """
        拿到订单截图界面
        :return:
        """
        screenshot_pic_result = './order_screenshot.png'

        # 截取手机当前屏幕
        exec_cmd('adb shell /system/bin/screencap -p /sdcard/screenshot.png')

        # 保存到PC端
        exec_cmd('adb pull /sdcard/screenshot.png %s' % screenshot_pic_result)

        return screenshot_pic_result


if __name__ == '__main__':
    taobao = TaoBao('小米')
    taobao.run()
