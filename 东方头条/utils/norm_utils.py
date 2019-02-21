#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: norm_utils.py 
@time: 2/18/19 14:18 
@description：普通工具类
"""
from datetime import datetime, timedelta
import time


def current_time():
    """
    当前时间
    :return:
    """
    return datetime.fromtimestamp(time.time()).strftime('%Y-%m-%d %H:%M:%S')
