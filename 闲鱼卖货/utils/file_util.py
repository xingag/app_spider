#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: file_util.py 
@time: 2019-10-01 16:02 
@description：TODO
"""
import os


def get_all_files(file_path):
    # 所有文件
    files = os.listdir(file_path)

    # 加入路径前缀
    all_files = [file_path + file for file in files]

    return all_files

