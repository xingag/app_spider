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
@time: 5/14/19 09:17 
@description：设备工具类
"""

import os
from PIL import Image
import time
import re


def find_devices():
    rst = os.popen('adb devices').read()
    devices = re.findall(r'(.*?)\s+device', rst)
    if len(devices) > 1:
        return devices[1:]
    else:
        # raise Exception('DeviceNotFound')
        return []


def kill_app(package_name):
    """
    关闭指定的应用
    :param package_name：例如东方头条【com.songheng.eastnews】
    :return:
    """
    # nowtime = os.popen('date')
    # print(nowtime.read())
    os.popen('adb shell am force-stop %s' % package_name)


def get_screen_shot_part_img(image_name):
    """
    获取手机的部分内容
    :return:
    """
    # 截图
    os.system("adb shell /system/bin/screencap -p /sdcard/screenshot.jpg")
    os.system("adb pull /sdcard/screenshot.jpg %s" % image_name)

    # 压缩图片
    img = Image.open(image_name).convert('RGB')

    # 图片的原宽、高(1080*2160)
    w, h = img.size

    # 截取部分，去掉其头像、其他内容
    img = img.crop((0, 0, 900, 1500))


    img.thumbnail((int(w / 1.5), int(h / 1.5)))

    # 保存到本地
    img.save(image_name)

    return image_name


def get_screen_shot_img1(filename):
    """
    截屏后直接保存到本地相册下，可以直接在手机相册中找到【没有延迟】，而且是显示在最新的位置
    :return:
    """
    # -p 后写的是保存在手机的路径。
    # 保存到本地相册下
    os.system('adb shell screencap -p /sdcard/Pictures/Screenshots/%s' % filename)



def start_my_app(package_name, activity_name):
    """
    打开应用
    adb shell am start -n com.tencent.mm/.ui.LauncherUI
    :param package_name:
    :return:
    """
    os.popen('adb shell am start -n %s/%s' % (package_name, activity_name))


def play_next_video():
    """
    下一个视频
    从下往上滑动
    :return:
    """
    os.system("adb shell input swipe 540 1300 540 500 100")


def switch_adb_keyboard():
    """
    切换到adb输入法
    :return:
    """
    os.system('adb shell ime set com.android.adbkeyboard/.AdbIME')


def click_page_position(tap_position):
    """
    点击某个坐标点
    :param tap_position: 屏幕上的坐标点
    :return:
    """
    os.system("adb shell input tap {} {}".format(tap_position[0], tap_position[1]))

