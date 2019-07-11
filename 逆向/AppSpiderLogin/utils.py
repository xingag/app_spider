#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: utils.py 
@time: 2019-06-25 18:00 
@description：
"""

import time


def get_unix_time(type_13):
    """
    获取时间戳
    :param type_13:10位、13位，是否是13位
    :return:
    """
    t = time.time()

    if type_13:

        millis = int(round(t * 1000))
    else:
        millis = int(t)

    return millis
