#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: poco_util.py 
@time: 2019-11-22 14:52 
@description：TODO
"""

import codecs
import json

from airtest.core.api import *

from utils.string_util import *


def get_current_ui_tree(poco):
    """
    获取当前界面的UI树
    :param poco:
    :return:
    """
    ui = poco.agent.hierarchy.dump()

    ui_format = json.dumps(ui, indent=4)

    # 写入到文件中
    with codecs.open('ui.json', mode='w', encoding='utf-8') as file:
        file.write(unicode_to_str(ui_format))


def element_is_exist(img_paths):
    """
    元素是否准备
    :return:
    """
    result = None
    for img_path in img_paths:
        element_sign_position = exists(Template(img_path))
        if element_sign_position:
            result = element_sign_position
            break

    return result
