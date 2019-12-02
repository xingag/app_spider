#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: xianyu.py 
@time: 2019-11-22 12:12 
@description：签到
"""

__author__ = "xingag"

from airtest.core.api import *

auto_setup(__file__)

from poco.drivers.ios import iosPoco
from utils.math_util import *
from utils.poco_util import *

class XianYu(object):

    def __init__(self):
        self.package = 'com.taobao.fleamarket'
        self.poco = None
        self.size = None

    def run(self):
        # 1、连接设备、打开应用
        self.__pre()

        # 2、应用签到
        self.__sign_app()

        # 3、宝贝擦亮
        self.__goods_top()

        # 4、鱼塘签到
        self.__sign_fishpond()

    def __pre(self):
        """
        初始化
        :return:
        """
        # 连接设备
        connect_device('iOS:///127.0.0.1:8100')
        self.poco = iosPoco()

        # 屏幕宽高 [640, 1136]
        self.size = self.poco.get_screen_size()

        # 打开设备
        home()
        stop_app(package=self.package)
        start_app(package=self.package, activity=None)

        # 主页元素
        element_home_tab1 = self.poco('闲鱼')
        element_home_tab2 = self.poco('鱼塘')
        element_home_tab3 = self.poco('消息')
        element_home_tab4 = self.poco('我的')
        element_home_tab5 = self.poco('发布')

        self.poco.wait_for_all(
            [element_home_tab1, element_home_tab2, element_home_tab3, element_home_tab4, element_home_tab5])

        print('主页加载完全~')

    def __sign_app(self):
        """
        应用签到
        :return:
        """
        # 点击进入到签到页面
        self.poco('闲鱼币, 签到换好礼').click()

        element_has_signed = self.poco(value='已签到')

        if not element_has_signed:
            self.poco(value='马上签到').click()

        print('应用签到成功!')

        self.poco.click([0.0703125, 0.06690140845070422])

    def __sign_fishpond(self):
        """
        鱼塘签到
        :return:
        """
        self.poco('鱼塘').click()

        # 切换到Tab：我加入的
        self.poco(value='我加入的').click()

        element_num = \
            self.poco("Window").child("Other").child("Other").child("Other").child("Other").child("Other").child(
                "Other")[
                0].child("Other").child("Other").child("Other").child("Other").child("Other").child("Other").child(
                "Other").offspring("ScrollView").child("Other").child("Other")[0].child("Other").child(
                "Other").offspring("Table").child("Cell")[4].children()[0]

        # 获取属性值
        # 一共加入的鱼塘数目
        fishpond_num = get_num_from_word(element_num.attr('value'))[0]

        print(f'一共加入:{fishpond_num}个鱼塘')

        # get_current_ui_tree(self.poco)

        # 进入鱼塘按钮的个数(初始显示6个)
        while True:
            element_static_texts = self.poco('Window').offspring("Table").offspring(name='StaticText')

            for index, item in enumerate(element_static_texts):
                item_content = item.attr('value')
                if item_content == '进鱼塘':
                    print(f'内容是:进入鱼塘按钮，进行处理!!!')
                    fishpond_body = element_static_texts[index - 1]
                    # print(fishpond_body.attr('value'))
                    fishpond_body.click()
                    # 每个鱼塘进行签到
                    self.__fishpond_sign()
                else:
                    pass
                    # print(f'内容是：{item_content},不处理')

    def __fishpond_sign(self):
        """
        鱼塘签到
        :return:
        """
        # 注意：签到按钮没法获取到，因此只能换成图片识别

        # 签名元素
        element_sign_position = element_is_exist(['./image/ic_sign.png', './image/ic_sign1.png'])

        if element_sign_position:
            print('准备签到')
            # 点击签到，注意使用相对比例坐标
            print(element_sign_position[0] / self.size[0])
            print(element_sign_position[1] / self.size[1])
            self.poco.click([element_sign_position[0] / self.size[0], element_sign_position[1] / self.size[1]])
        else:
            print('鱼塘已经签到！')

        # 鱼塘签到界面有两种形式
        # 返回有两种方式：点击左上角的返回、点击右上角的图标X
        element_close_btn1 = self.poco(type='Button', name='关闭')
        element_close_btn2 = self.poco(type='Button', name='返回')

        if element_close_btn2.exists():
            element_close_btn2.click()
        elif element_close_btn1.exists():
            element_close_btn1.click()

    def __goods_top(self):
        """
        宝贝擦亮
        :return:
        """
        self.poco('我的').click()

        # get_current_ui_tree(self.poco)

        # 截取用PS得到坐标位置
        self.poco.click([0.1875, 0.36971831])

        get_current_ui_tree(self.poco)

        find_count = 0

        # 查找擦亮元素
        while True:
            shine_btns = self.poco('Window').offspring("Table").offspring(name='StaticText', value='擦亮')

            if len(shine_btns) == 0:
                break
            else:
                for shine_btn in shine_btns:
                    # 一个个宝贝去擦亮
                    print('点击一个商品')
                    shine_btn.click()
            # 滑动到下一个页面
            self.poco.swipe([0.5, 0.8], [0.5, 0.3], duration=1)

            find_count += 1

        print('全部宝贝都已擦亮~')

        # 返回
        self.poco('返回').click()


if __name__ == '__main__':
    xianyu = XianYu()
    xianyu.run()
