#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: element_utils.py 
@time: 2020-03-14 11:35 
@description：元素工具类
"""
from appium_utils import *


def find_element_by_id_and_text(driver: WebDriver, id, text):
    """
    通过id和text查找元素
    :param driver:
    :param id:
    :param text:
    :return:返回查找后的元素
    """
    result = None

    elements = driver.find_elements_by_id(id)
    if len(elements) == 0:
        return result

    for element in elements:
        if element.get_attribute('text') == text:
            result = element
            break

    return result


def find_element_by_text(driver: WebDriver, text):
    """
    通过text属性查找元素
    :param driver:
    :param text:
    :return:
    """
    return driver.find_element_by_xpath("//android.widget.TextView[contains(@text,'" + text + "')]")


def element_perform_click(parentElement, id):
    """
    某个元素执行点击操作
    :param parentElement：WebDriver或者WebElement
    :param id:待查找的元素id
    :return:
    """
    element = parentElement.find_element_by_id(id)

    # 判断是否可以点击
    element_clickable = element.get_attribute("clickable")

    if element_clickable:
        element.click()
        return

    # 如果当前元素不可以点击，一直向上找可以点击的父类元素，执行点击操作
    while True:
        element = element.parent
        if element.get_attribute("clickable"):
            element.click()
            break


def get_element_text(parentElement, element_id):
    """
    获取元素的text属性值
    :param parentElement: WebElement或者WebDriver
    :param element_id: 元素id
    :return:
    """
    element = parentElement.find_element_by_id(element_id)

    return element.get_attribute("text")


def fb_id(driver: WebDriver, parentElement, element_id):
    """
    通过id查找元素
    :param driver:
    :param parentElement 父元素中查找
    :param element_id:
    :return:
    """
    while True:
        try:
            # 注意：查找单个元素经常容许产生异常，这里进行捕获后，然后滑动一次，继续查找
            element = parentElement.find_element_by_id(element_id)
            return element
        except:
            print('查找元素：【%s】产生异常，滑动一次，再进行查找！' % element_id)
            swipe_up_small(driver, 500)
