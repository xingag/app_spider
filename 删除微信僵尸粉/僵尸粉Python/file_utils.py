#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: file_utils.py 
@time: 2019-07-22 14:37 
@description：文件管理器
"""
import csv
import os


def read_csv(csv_file_name):
    """
    读取csv文件
    字典
    :return:
    """
    values = []
    with open(csv_file_name, 'r') as fp:
        reader = csv.DictReader(fp)

        for item in reader:
            value = {
                'userName': item['userName'],
                'alias': item['alias'],
                'nickName': item['nickName']
            }
            values.append(value)

    return values


def clean_file(filename):
    """
    清空文件
    :param filename:
    :return:
    clean_file('./data/black_list.txt')
    """
    f = open(filename, 'w')
    # 清空文件
    f.truncate()
    f.close()


def write_to_file(filename, content):
    """
    往文件中追加内容
    :param filename:
    :param content:
    :return:
    """
    file_write_obj = open(filename, 'a+')

    # 追加数据
    file_write_obj.writelines(content)
    file_write_obj.write('\n')
    file_write_obj.close()


def export_wx_db_from_phone(target_path):
    """
    从手机中导出通信录数据
    :param target_path:
    :return:
    """
    # 微信通信录数据
    wx_db_source_path = "/data/data/com.xingag.crack_wx/wx_data.csv"

    # 导出到本地
    os.popen('adb pull %s %s' % (wx_db_source_path, target_path))
