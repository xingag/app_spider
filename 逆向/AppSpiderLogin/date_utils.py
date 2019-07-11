#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: date_utils.py 
@time: 2019-07-06 16:53 
@description：日期工具类
"""

import datetime
import calendar


# 获取第一天和最后一天
def getFirstAndLastDay():
    # 获取当前年份
    year = datetime.date.today().year

    # 获取当前月份
    month = datetime.date.today().month

    # 获取当前月的第一天的星期和当月总天数
    weekDay, monthCountDay = calendar.monthrange(year, month)
    # 获取当前月份第一天
    firstDay = datetime.date(year, month, day=1)
    # 获取当前月份最后一天
    lastDay = datetime.date(year, month, day=monthCountDay)
    # 返回第一天和最后一天
    return firstDay, lastDay


def get_last_day():
    # 最后一天
    last_day = getFirstAndLastDay()[1]

    # 转为字符串
    return last_day.strftime('%Y%m%d')

