#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: appium_utils.py 
@time: 2020-03-14 14:41 
@description：来源：公众号【AirPython】，欢迎关注
"""
from appium.webdriver.webdriver import WebDriver


def getSize(driver: WebDriver):
    x = driver.get_window_size()['width']  # width为x坐标
    y = driver.get_window_size()['height']  # height为y坐标
    return (x, y)


def swipe_up(driver: WebDriver, peroid):
    """
    向上滑动
    :param driver:
    :param peroid:
    :return:
    """
    l = getSize(driver)
    x1 = int(l[0] * 0.5)
    y1 = int(l[1] * 0.75)
    y2 = int(l[1] * 0.5)
    driver.swipe(x1, y1, x1, y2, peroid)


def swipe_up_small(driver: WebDriver, peroid):
    """
    向上滑动(小距离)
    :param driver:
    :param peroid:
    :return:
    """
    l = getSize(driver)
    x1 = int(l[0] * 0.5)
    y1 = int(l[1] * 0.7)
    y2 = int(l[1] * 0.6)
    driver.swipe(x1, y1, x1, y2, peroid)


def swipe_up_with_distance(driver: WebDriver, distance, peroid):
    """
    向上滑动固定的距离
    :param driver:
    :param peroid:
    :return:
    """
    l = getSize(driver)
    x1 = int(l[0] * 0.5)
    y1 = int(l[1] * 0.9)
    y2 = int(l[1] * 0.9 - distance)
    driver.swipe(x1, y1, x1, y2, peroid)


def swipe_down(driver: WebDriver, peroid):
    """
    向下滑动
    :param driver:
    :param peroid:
    :return:
    """
    l = getSize(driver)
    x1 = int(l[0] * 0.5)
    y1 = int(l[1] * 0.25)
    y2 = int(l[1] * 0.75)
    driver.swipe(x1, y1, x1, y2, peroid)
