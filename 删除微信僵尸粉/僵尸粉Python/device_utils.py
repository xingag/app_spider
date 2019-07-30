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
@time: 2019-07-22 15:57 
@description：TODO
"""
import os


def start_my_app(package_name, activity_name):
    """
    打开应用
    :param package_name:
    :return:
    """
    os.popen('adb shell am start -n %s/%s' % (package_name, activity_name))
