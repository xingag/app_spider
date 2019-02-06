#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: Red_Wars.py 
@time: 2/5/19 09:59 
@description：抢红包
"""

# -*- encoding=utf8 -*-
__author__ = "xingag"

from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco

poco = AndroidUiautomationPoco(use_airtest_input=True, screenshot_each_action=False)

auto_setup(__file__)

# 1.打开微信
poco(text='微信').click()

# 2.输入要抢的群
# target = input('要抢哪个群:')
target = '抢红包'

# 3.获取消息列表名称
item_elements = poco(name='com.tencent.mm:id/b4m').offspring('com.tencent.mm:id/b4o')

names = list(map(lambda x: x.get_text(), item_elements))


def get_red_package():
    """
    发现红包，就立马执行点击操作
    :return:
    """
    # 1.获取消息列表元素
    msg_list_elements_pre = poco("android.widget.ListView").children()

    # 1.1 由于msg_list_elements属于UIObjectProxy对象，不能使用reverse进行反转
    # 利用For把消息取反
    msg_list_elements = []

    for item in msg_list_elements_pre:
        msg_list_elements.insert(0, item)

    # 2.从后面的消息开始遍历
    for msg_element in msg_list_elements:

        # 2.1 微信红包标识元素
        red_key_element = msg_element.offspring('com.tencent.mm:id/apf')

        # 2.2 是否已经领取元素
        has_click_element = msg_element.offspring('com.tencent.mm:id/ape')

        # 2.3 红包【包含：收到的红包和自己发出去的红包】
        if red_key_element:
            print('发现一个红包')
            if has_click_element.exists() and (
                    has_click_element.get_text() == '已领取' or has_click_element.get_text() == '已被领完'):
                print('已经领取过了，略过~')
                continue
            else:
                # 抢这个红包
                print('新红包，抢抢抢~')
                msg_element.click()

                click_element = poco("com.tencent.mm:id/cv0")
                if click_element.exists():
                    click_element.click()
                keyevent('BACK')
        else:
            print('红包元素不存在')
            continue


if target in names:
    index = names.index(target)
    # 点击进入群聊
    item_elements[index].click()

    while True:
        get_red_package()
        print('休眠1秒钟，继续刷新页面，开始抢红包。')
        sleep(1)
else:
    print('找到这个群')
