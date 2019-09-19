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
import cv2
import time
import re
import subprocess
from utils.cmd_utils import *


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
    os.popen('adb shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n %s/%s' % (
        package_name, activity_name))


def save_screenshot_to_pc(desc):
    """
    获取屏幕截图
    desc 截图保存路径
    :return:
    """
    exec_cmd('adb shell /system/bin/screencap -p /sdcard/screenshot.png')
    exec_cmd('adb pull /sdcard/screenshot.png %s' % desc)


def get_screenshot_part(position1, position2):
    """
    根据坐标，截取当前界面的一部分
    :param position1: 左上角坐标(x,y)
    :param position2: 右下角坐标(x,y)
    :return:
    """
    # 临时图片文件
    img_file_name = 'screenshot.png'

    # 裁剪后的图片
    img_part_path = "./part.jpg"

    # 保存到内存中
    os.popen('adb shell /system/bin/screencap -p /sdcard/%s' % img_file_name)
    # 下载到PC端
    os.popen('adb pull /sdcard/%s' % img_file_name)

    time.sleep(1)

    img = cv2.imread("./%s" % img_file_name)

    # (2160, 1080, 3)
    size = img.shape

    print(size)

    # 截取坐标
    # 裁剪坐标为[y0:y1, x0:x1]
    cropped = img[position1[1]:position2[1], position1[0]:position2[0]]

    # 截取到新文件中
    cv2.imwrite(img_part_path, cropped)

    # 删除临时文件
    # os.remove("./%s" % img_file_name)


def getScreenResolution():
    """
    获取设备屏幕分辨率，return (width, high)
    """
    pattern = re.compile(r"\d+")
    out = subprocess.Popen("adb shell dumpsys window displays |head -n 3", shell=True, stdout=subprocess.PIPE,
                           stderr=subprocess.PIPE).stdout.read()

    # 拿到所有数字
    display = pattern.findall(out.decode('utf-8'))

    # 分辨率：display[6]*display[7]，即1080*2164，密度480p
    return int(display[6]), int(display[7]), int(display[3])


def kill_all():
    """
    关闭所有的应用
    :return:
    """
    os.popen('adb shell am kill-all')


def get_ui_tree(poco):
    print('打印整个UI树')
    print('==' * 30)
    result = json.dumps(poco.agent.hierarchy.dump(), indent=4)
    return result


def write_ui_tree(poco, filepath):
    str = json.dumps(poco.agent.hierarchy.dump(), indent=4)
    with open(filepath, 'w', encoding='utf-8') as file:
        file.write(str)


def get_element_center_position(poco, bound):
    """
    获取元素的中心点坐标
    :return:
    """
    # 获取手机屏幕的宽、高
    screen_width = poco.get_screen_size()[0]
    screen_height = poco.get_screen_size()[1]

    # 元素的中心点坐标
    center_position = (bound[1] + bound[-1]) / 2 * screen_width, (
            bound[0] + bound[2]) / 2 * screen_height

    return center_position
