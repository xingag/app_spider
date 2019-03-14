#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: string_utils.py 
@time: 2/1/19 23:25 
@description：TODO
"""

import re
import random
import string


def filter_name(desstr, restr=''):
    """
    过滤特殊表情,只保留中文、英文、数字
    """

    # 一个网名为【......】的网友，随机生成为6位的字符
    if desstr == r'......':
        return make_random_string(6)

    return filter_emoji(restr, desstr)


def filter_emoji(desstr, restr=''):
    """
       过滤特殊表情,只保留中文、英文、数字
    """
    cop = re.compile("[^\u4e00-\u9fa5^.^a-z^A-Z^0-9]")

    return cop.sub(restr, desstr)


def make_random_string(num):
    """
    生成随机字符串
    :param num:
    :return:
    """
    return ''.join(random.sample(string.ascii_letters + string.digits, num))
