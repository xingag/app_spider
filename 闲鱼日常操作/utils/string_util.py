#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: string_util.py 
@time: 2019-11-23 10:28 
@description：TODO
"""


def unicode_to_str(unicode_str):
    """
    Unicode字符串转为中文
    注意：适用于Python3
    :param unicode_str:
    :return:
    """
    return unicode_str.encode('utf-8').decode('unicode_escape')
