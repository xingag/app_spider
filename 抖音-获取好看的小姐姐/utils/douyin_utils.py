#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: douyin_utils.py 
@time: 6/2/19 21:29 
@description：工具类
"""
from utils.adb.element import *
import time
import json
from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco


def is_a_ad():
    """
    判断的当前页面上是否是一条广告
    :return:
    """
    element = Element()
    ad_tips = ['去玩一下', '去体验', '立即下载']

    find_result = False

    for ad_tip in ad_tips:
        try:
            element_result = element.findElementByName(ad_tip)
            # 是一条广告,直接跳出
            find_result = True
            break
        except Exception as e:
            find_result = False

    return find_result


def wait_for_download_finished(poco):
    """
    从点击下载，到下载完全
    :return:
    """

    element = Element()
    while True:
        # 由于是对话框，不能利用Element类来判断是否存在某个元素来准确处理
        # element_result = element.findElementByName('正在保存到本地')

        # 当前页面UI树元素信息
        # 注意2：保存的时候可能会获取元素异常，这里需要抛出，并终止循环
        # com.netease.open.libpoco.sdk.exceptions.NodeHasBeenRemovedException: Node was no longer alive when query attribute "visible". Please re-select.
        try:
            ui_tree_content = json.dumps(poco.agent.hierarchy.dump(), indent=4).encode('utf-8').decode('unicode_escape')
        except Exception as e:
            print(e)
            print('异常，按下载处理~')
            break

        if '正在保存到本地' in ui_tree_content:
            print('还在下载中~')
            time.sleep(0.5)
            continue
        else:
            print('下载完成~')
            break
