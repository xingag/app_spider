#!/usr/bin/env python
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: element_util.py
@time: 2019-10-02 11:54
@description：元素工具类
"""
from airtest.core.api import *
import re
from utils.cmd_util import *
import xml.etree.cElementTree as ET


class Element(object):
    def __init__(self):
        # 定义匹配数字模式
        self.pattern = re.compile(r"\d+")

        self.path_ui_tree = './uidump.xml'

    def __get_ui_tree_to_local(self):
        """
        获取当前Activity控件树
        """
        exec_cmd("adb shell uiautomator dump /data/local/tmp/uidump.xml")
        exec_cmd("adb pull /data/local/tmp/uidump.xml ./")

    def __element(self, attr_name, name):
        """
        同属性单个元素，返回单个坐标中心点坐标
        """
        tree = ET.ElementTree(file=self.path_ui_tree)
        treeIter = tree.iter(tag="node")

        for elem in treeIter:
            if elem.attrib[attr_name] == name:
                bounds = elem.attrib["bounds"]
                coord = self.pattern.findall(bounds)
                Xpoint = (int(coord[2]) - int(coord[0])) / 2.0 + int(coord[0])
                Ypoint = (int(coord[3]) - int(coord[1])) / 2.0 + int(coord[1])

                return Xpoint, Ypoint

    def __get_element_position(self, attr_name, name):
        """
        返回元素的两个坐标，包含左上角坐标、右下角坐标
        :param attr_name:
        :param name:
        :return:
        """
        tree = ET.ElementTree(file=self.path_ui_tree)
        treeIter = tree.iter(tag="node")

        position_top_left, position_bottom_right = (0, 0), (0, 0)

        for elem in treeIter:
            # print(elem.attrib[attr_name])
            if elem.attrib[attr_name] == name:
                bounds = elem.attrib["bounds"]
                coord = self.pattern.findall(bounds)

                print('***' * 60)
                print(coord)
                print('***' * 60)

                position_top_left = (int(coord[0]), int(coord[1]))
                position_bottom_right = (int(coord[2]), int(coord[3]))
                break

        return position_top_left, position_bottom_right

    def __get_element_position_pro(self, attr1, value1, attr2, value2):
        """
        返回元素的两个坐标，包含左上角坐标、右下角坐标
        :param kwargs:多组属性，比如：text，id等
        :return:
        """
        tree = ET.ElementTree(file=self.path_ui_tree)
        treeIter = tree.iter(tag="node")

        position_top_left, position_bottom_right = (0, 0), (0, 0)

        for elem in treeIter:
            if elem.attrib[attr1] == value1 and elem.attrib[attr2] == value2:
                bounds = elem.attrib["bounds"]
                coord = self.pattern.findall(bounds)

                position_top_left = (int(coord[0]), int(coord[1]))
                position_bottom_right = (int(coord[2]), int(coord[3]))
                break

        return position_top_left, position_bottom_right

    def __elements(self, attrib, name):
        """
        同属性多个元素，返回坐标元组列表
        """
        list = []
        tree = ET.ElementTree(file=self.path_ui_tree)
        treeIter = tree.iter(tag="node")
        for elem in treeIter:
            if elem.attrib[attrib] == name:
                bounds = elem.attrib["bounds"]
                coord = self.pattern.findall(bounds)
                Xpoint = (int(coord[2]) - int(coord[0])) / 2.0 + int(coord[0])
                Ypoint = (int(coord[3]) - int(coord[1])) / 2.0 + int(coord[1])
                list.append((Xpoint, Ypoint))
        return list

    def get_current_ui_tree(self):
        """
        获取当前页面的UI树
        :return:
        """
        if os.path.exists(self.path_ui_tree):
            print('存在，先删除')
            os.remove(self.path_ui_tree)
        self.__get_ui_tree_to_local()

    def find_element_position_by_text(self, content):
        """
        通过文本内容获取元素的坐标
        :param content:
        :return:
        """
        return self.__get_element_position("text", content)

    def find_element_position_by_id(self, id):
        """
        通过文本内容获取元素的坐标
        :param content:
        :return:
        """
        return self.__get_element_position("resource-id", id)

    def find_elment_position_by_id_and_text(self, id, content):
        """
        通过id和text查找元素的坐标
        :param id:
        :param text:
        :return:
        """
        return self.__get_element_position_pro("resource-id", id, "text", content)

    def find_elment_position_by_id_and_classname(self, id, class_name):
        """
        通过id和text查找元素的坐标
        :param id:
        :param text:
        :return:
        """
        return self.__get_element_position_pro("resource-id", id, "class", class_name)

    def find_elment_position_by_id_and_index(self, id, index):
        """
        通过id和index查找元素的坐标
        :param id:
        :param text:
        :return:
        """
        return self.__get_element_position_pro("resource-id", id, "index", index)

    def findElementByName(self, name):
        """
        通过元素名称定位
        usage: findElementByName(u"设置")
        """
        return self.__element("text", name)

    def findElementsByName(self, name):
        return self.__elements("text", name)

    def findElementByClass(self, className):
        """
        通过元素类名定位
        usage: findElementByClass("android.widget.TextView")
        """
        return self.__element("class", className)

    def findElementsByClass(self, className):
        return self.__elements("class", className)

    def findElementById(self, id):
        """
        通过元素的resource-id定位
        usage: findElementsById("com.android.deskclock:id/imageview")
        """
        return self.__element("resource-id", id)

    def findElementsById(self, id):
        return self.__elements("resource-id", id)


def perform_click(element):
    """
    点击某个元素
    :param element:
    :return:
    """
    if not element:
        return
    while element:
        # 如果可以点击，就直接进行点击；否则，获取父类元素，执行点击操作
        if element.attr('enabled'):
            element.click()
            break
        else:
            element = element.parent()


def perform_back(poco, id):
    """
    返回，直到看到什么元素
    :param stop_visiable_element:
    :return:
    """

    while not poco(id):
        # print('不存在这个元素')
        keyevent('BACK')
        time.sleep(2)


def get_child_element(parent_element, child_element_id):
    """
    得到子孙元素
    item.offspring('com.taobao.idlefish:id/title_img')
    :return:
    """
    if parent_element:
        for element in parent_element.children():
            # 判断id
            if element.attr('resourceId') == child_element_id:
                return element
            else:
                parent_element = element
