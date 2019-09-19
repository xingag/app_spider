#!/usr/bin/env python
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: async_utils.py
@time: 2/13/19 11:53
@description：异步函数装饰器
"""

from threading import Thread, Timer
from time import sleep
from datetime import datetime


def async_f(f):
    """
    线程异步装饰器
    :param f:
    :return:
    """

    def wrapper(*args, **kwargs):
        thr = Thread(target=f, args=args, kwargs=kwargs)
        thr.start()

    return wrapper


def async_t(f):
    """
    定期器异步装饰器
    :param f:
    :return:None
    """

    def wrapper(*args, **kwargs):
        thr = Timer(5, f, args, kwargs)
        thr.start()

    return wrapper


def printTime(inc):
    """
    定时任务
    :param inc: 5秒执行一次
    :return:
    """
    # 任务：打印时间
    print(datetime.now().strftime("%Y-%m-%d %H:%M:%S"))

    t = Timer(inc, printTime, (inc,))
    t.start()
