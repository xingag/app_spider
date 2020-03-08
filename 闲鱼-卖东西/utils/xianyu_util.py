#!/usr/bin/env python
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: xianyu_util.py
@time: 2019-10-02 12:56
@description：TODO
"""
import os
import csv
import codecs
import math


def get_num(content):
    """
    从文字中获取到具体是数字
    :param content:
    :return:
    """
    return content[:content.index('人')]


def get_remain_time(remain_time):
    """
    剩余时长
    :param remain_time:
    :return:
    """
    remain_minute = int(remain_time / 60)

    print(remain_minute)

    remain_second = remain_time - remain_minute * 60

    remain_minute_content = '-' if remain_minute == 0 else str(remain_minute)

    remain_str = '爬虫剩余时长：%s分%d秒' % (remain_minute_content, remain_second)

    return remain_str


def cut_title(title, num):
    """
    缩减标题
    :param title:
    :num 只取前面固定数目
    :return:
    """
    # 去除空格
    title = title.replace(" ", "")

    # 截取
    return title[:num] if len(title) > num else title


def write_to_csv(file_path, values, write_head):
    """
    写入数据到csv中
    :param title:
    :param want_num:
    :param href:
    :return:
    """

    headers = ['title', 'num', 'share_url']

    with open(file_path, 'a+', encoding='utf-8', newline='') as fp:
        writer = csv.writer(fp)
        if write_head:
            writer.writerow(headers)
        else:
            writer.writerows(values)
