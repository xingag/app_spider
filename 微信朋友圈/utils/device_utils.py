#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: device_utils.py 
@time: 2/11/19 18:28 
@description：
"""

import os
import json


def kill_app(package_name):
    """
    关闭指定的应用
    :param package_name：例如东方头条【com.songheng.eastnews】
    :return:
    """
    # nowtime = os.popen('date')
    # print(nowtime.read())
    os.popen('adb shell am force-stop %s' % package_name)


def start_my_app(package_name, activity_name):
    """
    打开应用
    adb shell am start -n com.tencent.mm/.ui.LauncherUI
    :param package_name:
    :return:
    """
    os.popen('adb shell am start -n %s/%s' % (package_name, activity_name))


def kill_all():
    """
    关闭所有的应用
    :return:
    """
    os.popen('adb shell am kill-all')


def print_ui_tree(poco):
    print('打印整个UI树')
    print('==' * 30)
    print(json.dumps(poco.agent.hierarchy.dump(), indent=4))


def write_ui_tree(poco):
    str = json.dumps(poco.agent.hierarchy.dump(), indent=4)
    with open('log.txt', 'w', encoding='utf-8') as file:
        file.write(str)
