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
import numpy

# 白色
color_white = numpy.array([255, 255, 255])
# 黑色
color_black = numpy.array([0, 0, 0])


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


def get_space_index(arrs):
    """
    获取索引
    :param arr:
    :return:
    """

    # 分为True、False 两个数组
    index_true = []
    index_false = []

    for i, item in enumerate(arrs):
        if item:
            index_true.append(i)
        else:
            index_false.append(i)

    # 临时变量
    index_true_temp = index_true[0]

    # 间断的索引号
    index_result = 0

    # 从第二项开始计算
    for index, item in enumerate(index_true[1:]):
        # 连续的数字，不用管
        if item - index_true_temp == 1:
            pass
        # 非连续的数字，需要记下来
        else:
            index_result = index
            break
        index_true_temp = item

    # 主图x轴坐标开始的位置和结束的位置
    result = index_true[index_result], index_true[index_result + 1]

    print(result)

    return result


def crop_main_img(img_path):
    """
    获取主图
    :return:
    """
    img = cv2.imread(img_path)
    # 图片大小(高、宽、the pixels value is made up of three primary colors)
    size = img.shape

    img_height = size[0]
    img_width = size[1]
    channels = size[2]

    # 1080*458
    print(f'图片宽度:{img_width},高度:{img_height}')

    # 标识数组,针对x轴和y轴
    arr_x = []
    arr_y = []

    # 遍历宽，得到主图的x轴坐标
    for x in range(img_width):
        is_black = True

        # 遍历高
        for y in range(img_height):
            # 获取颜色值
            color_position = img[y, x]
            if (color_position == color_white).all():
                pass
            else:
                is_black = False

        arr_x.append(is_black)

    # 遍历高，得到主图的y轴坐标
    for y in range(img_height):
        is_black = True

        # 遍历高
        for x in range(img_width):
            # 获取颜色值
            color_position = img[y, x]
            if (color_position == color_white).all():
                pass
            else:
                is_black = False

        arr_y.append(is_black)

    position_x = get_space_index(arr_x)
    position_y = get_space_index(arr_y)

    main_img_path = "./head_img.jpeg"

    # 剪切
    # 裁剪坐标为[y0:y1, x0:x1]
    cropped = img[position_y[0]:position_y[1], position_x[0]: position_x[1]]
    cv2.imwrite(main_img_path, cropped)

    return main_img_path
