#!/usr/bin/env python  
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: wevideo.py
@time: 2019-09-11 10:29
@description：微视
"""

from utils.image_utils import *
from utils.device_utils import *
from utils.file_utils import *
from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
import threading
from enum import Enum
import logging


class TypeRedPackage(Enum):
    Norm = 0  # 普通红包
    MORE = 1  # 答题红包
    No = 2  # 没有红包


class WeVideo(object):

    def __init__(self):
        self.package_name = 'com.tencent.weishi'
        self.home_activity = 'com.tencent.oscar.module.splash.SplashActivity'

        # 配置日志
        LOG_FORMAT = "%(asctime)s - %(levelname)s - %(message)s"
        logging.basicConfig(filename='my.log', level=logging.DEBUG, format=LOG_FORMAT)

        # 互动红包元素【普通红包】
        self.image_red_package = './pic/red.png'

        # 互动红包元素【答题红包】
        self.image_red_package2 = './pic/red2.png'

        # 抢红包的切图元素【小红包】
        self.catch_red_package = './pic/red_catch.jpg'

        # 抢红包的切图元素【中红包】
        self.catch_red_package_more = './pic/red_catch_more.png'

        # 抢红包的切图元素【大红包】
        self.catch_red_package_most = './pic/red_catch_most.png'

        # 获得红包的元素
        self.get_red_package = './pic/part.jpg'

        self.wait_for_dialog_timeout = 10

        # 截图临时保存路径
        self.screenshot_pic_temp_path = './sc.jpg'

        self.poco = AndroidUiautomationPoco(screenshot_each_action=False)

        auto_setup(__file__)

    def __page_has_red_package(self):
        """
        当前视频是否包含红包
        :return:
        """
        # 截图当前页面
        save_screenshot_to_pc(self.screenshot_pic_temp_path)

        # 拿红包的截图去匹配
        result1 = find_image(self.screenshot_pic_temp_path, self.image_red_package)

        result2 = find_image(self.screenshot_pic_temp_path, self.image_red_package2)

        # 删除临时文件
        remove_cache(self.screenshot_pic_temp_path)

        # 不包含红包元素
        if result1:
            type_result = TypeRedPackage.Norm
            print("【普通红包】视频包含红包")
        elif result2:
            type_result = TypeRedPackage.MORE
            print("【答题红包】视频包含红包")
        else:
            type_result = TypeRedPackage.No
            print("【没有红包】下一条！！！")
            print('==' * 30)
        return type_result

    def run(self):
        # 打开应用
        self.__open_app()

        # 异步处理对话框
        threading.Thread(target=self.__handle_dialog, name='thread1').start()

        # 等待到达主页面
        self.poco('com.tencent.weishi:id/tv_home_tab_recommend', text=u'推荐').wait_for_appearance()

        print('到达首页')

        # 判断当前视频包含红包
        while True:
            print('==' * 30)
            print('开始新的一个视频')
            # 让视频缓存2s
            time.sleep(3)
            video_type = self.__page_has_red_package()
            if video_type == TypeRedPackage.Norm:
                # 抢普通红包
                self.__catch_red_package()
            elif video_type == TypeRedPackage.MORE:
                # 抢答题红包
                # 先答题
                self.__to_answer_question()

            # 滑动到下一个视频
            self.poco.swipe([0.5, 0.8], [0.5, 0.1], duration=0.2)

    def __open_app(self):
        """
        打开应用
        :return:
        """
        home()
        # stop_app(self.package_name)
        start_my_app(self.package_name, self.home_activity)

    def __handle_dialog(self):
        """
        处理警告对话框
        :return:
        """
        count = 0
        while count < self.wait_for_dialog_timeout:
            tip_notice = self.poco('com.tencent.weishi:id/title_text', text=u'青少年保护功能提示')
            try:
                if tip_notice.exists():
                    # 关闭
                    print('出现警告对话框，关闭之。')
                    self.poco('com.tencent.weishi:id/close_btn').click()
                    break
                else:
                    pass
            except Exception as e:
                print('产生异常了')

            time.sleep(1)
            count += 1

        # print('处理对话框时间到！！！')

    def __catch_red_package(self):
        """
        抢红包
        :return:
        """

        # 第一步：先找到红包的位置(有bug)
        # while True:
        #     # 截图匹配红包
        #     save_screenshot_to_pc(self.screenshot_pic_temp_path)
        #     # 匹配普通、大红包
        #     result1 = find_image(self.screenshot_pic_temp_path, self.catch_red_package)
        #     result2 = find_image(self.screenshot_pic_temp_path, self.catch_red_package_more)
        #     result3 = find_image(self.screenshot_pic_temp_path, self.catch_red_package_most)
        #     result = None
        #     if result1:
        #         result = result1
        #     elif result2:
        #         result = result2
        #     elif result3:
        #         result = result3
        #
        #     if result:
        #         print('找到红包！红包坐标如下：')
        #         print(result)
        #         break
        #     else:
        #         print('没有找到红包')

        while True:
            vp = self.poco('com.tencent.weishi:id/hippy_container')
            if vp.exists():
                # 元素
                try:
                    red_package_element = vp.children()[0].children()[0].children()[0].children()[0]
                except Exception:
                    print('产生一个异常')
                    continue

                # 获取bound()属性
                element_size = red_package_element.get_bounds()

                center_position = get_element_center_position(self.poco, element_size)

                # 一直等待红包元素出现，才执行点击操作
                if len(red_package_element.children()) > 0:
                    print(center_position)
                    break
                else:
                    # print('等待红包出现可以点击')
                    pass
            else:
                print('vp不存在')

        # 获取到红包坐标之后，执行点击点击操作，直到抢到红包为止
        exec_cmd('adb shell input tap %d %d' % (center_position[0], center_position[1]))

        time.sleep(3)

        # 红包元素图片匹配
        # while True:
        #     print('点击抢红包')
        #     # exec_cmd('adb shell input tap %d %d' % (result[0], result[0]))
        #     exec_cmd('adb shell input tap %d %d' % (center_position[0], center_position[1]))
        #     exec_cmd('adb shell input tap %d %d' % (center_position[0], center_position[1]))
        #     save_screenshot_to_pc(self.screenshot_pic_temp_path)
        #     result_get_red_package = find_image(self.screenshot_pic_temp_path, self.get_red_package)
        #
        #     if result_get_red_package:
        #         print('成功获取到红包')
        #         break
        #     else:
        #         print('没有获取红包，继续点击')

    def __getLastChild(self, vp):
        """
        遍历获取最后一级的Child
         # top, right, bottom, left
        :param vp:
        :return:
        """

        temp = vp
        while True:
            children_size = len(temp.children())
            if children_size == 0:
                break
            else:
                temp = temp.children()[0]
                print(temp.get_position())

        return temp


def __to_answer_question(self):
    """
    去答题
    :return:
    """
    # 一般视频不会超过30s
    self.poco('com.tencent.weishi:id/ab_content_name').wait_for_appearance(30)

    # 标题
    question_title_element = self.poco('com.tencent.weishi:id/ab_content_name')

    question_title = question_title_element.get_text()

    print('标题是:%s' % question_title)

    # 所有选项元素
    question_answer_elements = question_title_element.parent().children()[1].children()

    question_answers = []

    for question_answer_element in question_answer_elements:
        question_answers.append(question_answer_element.get_text())

    print(question_answers)


if __name__ == '__main__':
    wevideo = WeVideo()
    wevideo.run()
