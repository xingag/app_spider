#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: Des_utils.py 
@time: 2019-07-04 14:59 
@description：TODO
"""
from Cryptodome.Cipher import DES
from Cryptodome.Util.Padding import pad, unpad
import base64


# 依赖：pip3 install pycryptodomex

def encode(arg1, arg2):
    """
    加密
    :param arg1:11位手机号码|20190704 string
    :param arg2:64230704  bytes
    :return:
    """
    des = DES.new(arg2, mode=DES.MODE_CBC, iv=bytearray([1, 2, 3, 4, 5, 6, 7, 8]))
    msg = des.encrypt(pad(arg1.encode(), DES.block_size))

    # 加密后的结果,bytes
    encode_result = base64.b64encode(msg)

    # 转为string
    return str(encode_result, encoding='utf-8')
