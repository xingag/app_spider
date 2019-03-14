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
@time: 2/1/19 14:57 
@description：文件管理器
"""

import os
import csv
import codecs
import time

headers = ['nickname', 'msg', 'pub_time']


def del_files(path):
    """
    删除某个文件夹下的所有文件
    :param path:
    :return:
    """
    os.popen('adb shell rm -r %s*' % path)


def del_folder(path):
    """
    删除某个文件夹及下面的所有文件
    :param path:
    :return:
    """
    os.popen('adb shell rm -r %s' % path)


# 注意：使用adb没法排序文件，为了准确获取文件，下载文件之前，需要提前删除微信文件夹
def copy_last_pic_to_local(path, folder):
    """
     从移动端获取到最新的一个图片
    :param path: 手机上的文件目录
    :param folder:PC端文件保存的目录
    :return:
    """
    # 读取目录下的所有文件
    while True:
        r = os.popen('adb shell ls %s' % path)
        # 读取命令行的输出到一个list
        infos = r.readlines()

        # 注意：由于网速慢的时候，文件不一定已经保存成功，需要做一定的延时处理
        if len(infos) > 0:
            break
        else:
            time.sleep(1)

    # 文件名称
    last_file_name = infos[0].strip('\r\n')

    print(path + last_file_name)
    print(folder)

    if not os.path.exists(folder):
        os.makedirs(folder)

    # 加上绝对路径，把文件复制到本地文件夹中
    os.popen('adb pull %s %s' % (path + last_file_name, folder))


def write_to_csv(first, format_values):
    """
    写入到csv文件中
    :return: 
    """
    with open('firends_circle.csv', 'a', encoding='utf-8-sig', newline='') as fp:

        # 1.创建一个dictwriter对象
        writer_dict = csv.DictWriter(fp, headers)

        # 2.手动写入标题
        if first:
            writer_dict.writeheader()
        else:
            # 3.写入数据
            writer_dict.writerows(format_values)

