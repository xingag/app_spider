#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: airtest_utils.py 
@time: 2/10/19 14:34 
@description：常用工具类
"""
from airtest.core.api import *


def back_keyevent():
    """
    按【返回键】
    :return:
    """
    keyevent('BACK')


def home_keyevent():
    keyevent("HOME")
