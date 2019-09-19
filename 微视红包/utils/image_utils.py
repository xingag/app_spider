#!/usr/bin/env python
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: image_utils.py
@time: 2019-09-12 12:21
@description：TODO
"""

import cv2
import aircv as ac


@DeprecationWarning
def find_image_cv_unused(source_path, part_path):
    """
    注意：匹配度还存在一定误差，废弃
    :param source: 原图
    :param part:待匹配的图片
    :return:
    """
    # 原始图像
    source = ac.imread(source_path)

    # 待查找的部分
    part = ac.imread(part_path)

    # result = cv2.matchTemplate(source, part, cv2.TM_CCOEFF_NORMED)
    result = cv2.matchTemplate(source, part, cv2.TM_CCORR_NORMED)

    pos_start = cv2.minMaxLoc(result)[3]

    x = int(pos_start[0]) + int(part.shape[1] / 2)
    y = int(pos_start[1]) + int(part.shape[0] / 2)

    # 相似度
    similarity = cv2.minMaxLoc(result)[1]

    print("相似度:%f" % similarity)

    if similarity < 0.85:
        return -1, -1
    else:
        return [(x, y), (x, y)]


def find_image(source_path, part_path):
    """
    匹配模板
    :param source_path: 原图片
    :param part_path: 待匹配的图片
    :return:
    """
    # 原始图像
    source = ac.imread(source_path)

    # 待查找的部分
    part = ac.imread(part_path)

    result_raw = ac.find_template(source, part)

    # 匹配图片中心点坐标
    if result_raw and result_raw.get('confidence') >= 0.8:
        center_position = result_raw.get('result')
        print(result_raw)
    else:
        center_position = None
    return center_position
