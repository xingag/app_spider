#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: md5_utils.py 
@time: 2019-07-06 11:43 
@description：TODO
"""

import hashlib


def md5(value):
    """
    md5加密
    :param value:
    :return:
    """
    m = hashlib.md5()
    m.update(str.encode(value))
    sign = m.hexdigest()

    return sign
