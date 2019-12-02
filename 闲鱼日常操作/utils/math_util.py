#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: math_util.py 
@time: 2019-11-22 16:20 
@description：TODO
"""

import re


def get_num_from_word(content):
    """
    从文本中获取数字
    :param content:
    :return:
    """
    print('原始数据为:' + content)

    result_pre = re.findall(r"\d+", content)

    result = list(map(lambda item: int(item), result_pre))

    return result
