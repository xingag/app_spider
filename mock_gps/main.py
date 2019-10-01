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
@time: 2019-10-01 12:28
@description：TODO
"""

__author__ = "xingag"

from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from utils.device_utils import *
from utils.file_util import *

# -------------------------------------------------
# FackLocation App
package_name_location = 'com.lerist.fakelocation'
activity_location = '.ui.activity.MainActivity'

# 微信 App
package_name_weixin = 'com.tencent.mm'
activity_weixin = 'com.tencent.mm.ui.LauncherUI'


# -------------------------------------------------


class Mock_GPS(object):

    def __init__(self):
        self.poco = AndroidUiautomationPoco(screenshot_each_action=False)
        auto_setup(__file__)

        self.location = '故宫'

        self.msg = "这是一条心情"

        # 图片数目
        self.image_num = 1

    def run(self, location, msg):
        # 1、打开FackLocation，模拟位置
        self.location = location
        self.msg = msg

        self.__mock_location()

        # 2、开启模拟
        # 判断模拟位置是否打开，如果没有打开，需要进行打开操作
        self.__start_mock()

        # 3、回到桌面
        home()

        # 4、导入图片到手机相册内
        self.__import_image_to_dcim()

        # 5、打开微信
        stop_app(package_name_weixin)
        start_my_app(package_name_weixin, activity_weixin)

        # 6、打开朋友圈
        self.__open_friend_circle()

        # 7、选择照片
        self.__choose_photos()

        # 8、编辑内容，选择地理位置
        self.__put_content_and_gps()

        # 9、执行发送操作
        self.poco('com.tencent.mm:id/lm',text='发表').click()

    def __mock_location(self):
        """
        模拟定位
        :return:
        """
        home()
        stop_app(package_name_location)
        start_my_app(package_name_location, activity_location)

        # 点击添加位置
        self.poco('com.lerist.fakelocation:id/fab').click()

        # 点击搜索
        self.poco('com.lerist.fakelocation:id/m_item_search').click()

        # 输入框输入目的地
        self.poco('com.lerist.fakelocation:id/l_search_panel_et_input').set_text(self.location)

        # 等待搜索列表出现
        # self.poco('').wait_for_appearance()

        sleep(2)

        # 宽、高
        size = self.poco.get_screen_size()

        # 由于选择结果UI树查找不到，这里使用坐标来执行点击操作
        adb_click(500, 283)

        # 确定位置
        while self.poco('com.lerist.fakelocation:id/a_map_btn_done').exists():
            self.poco('com.lerist.fakelocation:id/a_map_btn_done').click()

    def __start_mock(self):
        """
        开启模拟位置
        :return:
        """
        mock_element = self.poco('com.lerist.fakelocation:id/f_fakeloc_tv_service_switch')
        if mock_element.get_text() == '启动模拟':
            mock_element.click()
            # 等待启动模拟完成
            self.poco('com.lerist.fakelocation:id/f_fakeloc_tv_service_switch', text='停止模拟').wait_for_appearance()
        else:
            pass
        print('模拟已经开启')

    def __open_friend_circle(self):
        """
        打开朋友圈
        :return:
        """
        # 等待完全打开微信App
        self.poco(text='微信').wait_for_appearance()
        self.poco(text='通讯录').wait_for_appearance()
        self.poco(text='发现').wait_for_appearance()
        self.poco(text='我').wait_for_appearance()

        print('微信完全打开')

        # 点击【发现】Tab
        self.poco('com.tencent.mm:id/djv', text='发现').parent().click()

        # 打开朋友圈
        self.poco('android:id/title', text='朋友圈').click()

        # 等待朋友圈动态加载完全
        self.poco('com.tencent.mm:id/eyx').wait_for_appearance()

    def __import_image_to_dcim(self):
        """
        导入图片到手机相册内
        :return:
        """

        # 本地的图片
        files = get_all_files('./image/')

        # 一共待发送的图片数目
        self.image_num = 9 if len(files) > 9 else len(files)

        # 手机相册目录
        phone_image_path = 'sdcard/DCIM/Camera/'

        # 一张一张图片导入到手机相册内
        for file in files[:self.image_num]:
            exec_cmd('adb push %s %s' % (file, phone_image_path))

        # 注意：需要发送广告，通知更新相册
        exec_cmd(
            'adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///%s' % phone_image_path)

        print('图片导入到相册成功！')

    def __choose_photos(self):
        """
        选择图片
        :return:
        """
        # 点击右上角的相机图标
        self.poco('com.tencent.mm:id/ln').click()

        # 选择相册
        self.poco(text='从相册选择').click()

        # 选择指定数目的照片
        cbs = self.poco('com.tencent.mm:id/ek8').offspring('com.tencent.mm:id/bwn')

        index = 0

        # 选中固定数目的照片
        for cb in cbs:
            if index < self.image_num:
                cb.click()
            else:
                break
            index += 1

        # 确认选择图片
        self.poco('com.tencent.mm:id/lm').click()

    def __put_content_and_gps(self):
        """
        输入内容和定位
        :return:
        """
        # 输入朋友圈内容
        self.poco('com.tencent.mm:id/d3k').set_text(self.msg)

        # 定位的次数，一般需要两次
        location_count = 0
        # 点击定位图标
        while True:

            self.poco('com.tencent.mm:id/d0a', text='所在位置').click()

            # 等待搜索列表中有结果出现
            self.poco('com.tencent.mm:id/du7').wait_for_appearance()

            if location_count == 0:
                # 返回
                keyevent('BACK')
                location_count += 1
            else:
                # 排除ListView的前两项（不显示、市区），直接点击第三项（具体位置）
                self.poco('com.tencent.mm:id/dul').children()[2].click()
                break


if __name__ == '__main__':
    mock_gps = Mock_GPS()
    # 地点
    location = '天安门广场'
    # 心情动态
    msg = '第一次来北京近距离看阅兵，进入到了内场，广场上好多解放军叔叔啊~\n阅兵结束后，有幸和大大握手了，感觉好值~'
    mock_gps.run(location, msg)
