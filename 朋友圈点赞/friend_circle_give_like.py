#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: main.py 
@time: 2020-03-13 10:30 
@description：朋友圈点赞机器人
"""
import logging
import time
import traceback

import yaml
from appium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

from CAPS import caps
from element_utils import *
from nlp_utils import *

# 来源：公众号【AirPython】，欢迎关注

class GiveLike(object):

    def __init__(self):

        self.driver = webdriver.Remote("http://127.0.0.1:4723/wd/hub", caps)

        self.driver.implicitly_wait(10)

        # 所有处理过的消息
        self.news_handled = []

        logging.basicConfig(filename='./log.txt', level=logging.DEBUG,
                            format='%(asctime)s - %(levelname)s - %(message)s')

        # 配置文件
        self.tag_id = None
        self.tag_text = None

    def __load_config(self):
        """
        初始化配置文件
        :return:
        """
        with open("./config.yaml", "r") as yaml_file:
            yaml_obj = yaml.load(yaml_file.read())
            self.tag_id = yaml_obj["tag"]["id"]
            self.tag_text = yaml_obj["tag"]["text"]

    def start(self):
        # 0、获取屏幕的高度
        self.__load_config()

        # 1、进入朋友圈
        self.__open_friend_circle()

        # 2、等待朋友圈加载完全
        # id：内容列表
        self.__wait_for_appear(self.tag_id['id_page_friend_circle_listview'])

        # 3、第一次滑动
        # 为了保证第一条动态能被正常处理，需要保证完整加载
        self.swipe_first(self.tag_id['id_page_friend_circle_listview'])

        # 4、循环查询朋友圈动态心情
        self.__cyclic_query(self.tag_id['id_page_friend_circle_item'])

    def __open_friend_circle(self):
        """
        打开朋友圈
        :return:
        """
        # 点击发现Tab
        find_element_by_id_and_text(self.driver, self.tag_id["id_page_main_discover"],
                                    self.tag_text["discover"]).click()

        # 进入朋友圈
        find_element_by_text(self.driver, self.tag_text["friend_circle"]).click()

    def __wait_for_appear(self, id):
        """
        等待某个元素出现
        :param id:
        :return:
        """
        # 显示等待 10s，直到元素出现
        WebDriverWait(self.driver, 30).until(
            EC.visibility_of_element_located((By.ID, id))
        )

    def __cyclic_query(self, id_item):
        """
        循环遍历
        :param id_item: 列表每一项
        :return:
        """
        while True:
            elements = self.driver.find_elements_by_id(id_item)
            print('==' * 20)
            print("当前页面一共有：%d条动态" % len(elements))

            for index, element in enumerate(elements):
                # 动态的类型：纯文字、纯图片、纯视频、纯链接、文字+图片、文字+视频、文字+链接、文字+音乐
                # 注意：必须保证元素都可以见，才不会导致异常
                try:
                    dynamic_contents = self.__get_dynamic_content(element)
                except Exception as e:
                    err_tag = "头部元素" if index == 0 else "尾部元素"
                    err = "**********%s产生一个异常**********" % err_tag

                    print(err)
                    logging.error(err)
                    logging.error(traceback.format_exc())

                    # 判断是页面的第一个元素还是最后一个元素
                    if index == 0:
                        continue
                    else:
                        break

                print('这条动态的内容如下：')
                print(dynamic_contents)

                # 判断这条动态是否处理过
                if dynamic_contents in self.news_handled:
                    print('*****这条动态处理过了，过滤*****')
                    continue

                # 加入列表，代表处理过
                self.news_handled.append(dynamic_contents)

                # 如果文本存在，并且是消极的，就不处理
                if dynamic_contents[2] and get_word_nlp(dynamic_contents[2]):
                    print('消极的内容，不点赞！')
                    continue

                # 点击，弹出点赞按钮
                element_perform_click(element, self.tag_id['id_page_friend_circle_approve_button_pre'])

                # 不点赞的情况：已经点过赞、有文字内容并且为消极
                # 未点赞：赞；已赞：取消
                try:
                    approve_text = get_element_text(self.driver, self.tag_id["id_page_friend_circle_approve_status"])
                except:
                    print('查找点赞状态产生异常')
                    if index == 0:
                        continue
                    else:
                        break

                if approve_text == '取消':
                    # 关闭点赞弹框
                    print('已经点赞过，不点赞')
                    element_perform_click(element, self.tag_id['id_page_friend_circle_approve_button_pre'])
                    continue

                # 注意：点赞按钮不属于这个元素
                # 注意，点赞按钮没法执行点击操作，需要往上找父类元素执行点击操作
                try:
                    # 一个有效的点赞
                    print('执行点赞操作！')
                    element_perform_click(self.driver, self.tag_id['id_page_friend_circle_approve_button'])
                except:
                    # 注意：如果点赞按钮不可见，就会抛出异常，可以在滚动后继续操作
                    print('点赞按钮不可见，点击产生异常')
                    pass

            # 往下继续滑动
            print('继续查看后面的内容')
            swipe_up(self.driver, 500)
            time.sleep(2)

    def __get_dynamic_content(self, element):
        """
        获取动态的类型
        :param element:
        :return:
        """
        # 文字的id：
        # 注意：不确定是否存在的元素，要使用find_elements_**,否则会抛出异常
        element_titles = element.find_elements_by_id(self.tag_id['id_page_friend_circle_item_title'])

        # 好友名
        element_author = element.find_element_by_id(self.tag_id['id_page_friend_circle_item_friend_name'])

        # 发布时间
        # 注意：可能没法找到，导致异常
        element_publish_time = element.find_element_by_id(self.tag_id['id_page_friend_circle_item_publish_time'])

        author_name = element_author.get_attribute("text")
        publish_time = element_publish_time.get_attribute("text")
        content = None

        if len(element_titles) > 0:
            content = element_titles[0].get_attribute('text')

        # 返回发布者、发布时间、发布内容
        return author_name, publish_time, content

    def swipe_first(self, id_listview):
        """
        首次滑动
        :param param:
        :return:
        """
        element_listview = self.driver.find_element_by_id(id_listview)

        # 由于从第二子元素开始，获取到第一个子元素的高度
        element_content = element_listview.find_element_by_class_name("android.widget.LinearLayout")

        # 获取元素的属性
        size = element_content.size
        # location = element_content.location

        # 滑动一次
        # 由于滑动因为滑动速度存在误差，这里滑动距离需要做一下处理
        swipe_up_with_distance(self.driver, size.get("height") - 50, 1000)

        time.sleep(2)


if __name__ == '__main__':
    give_like = GiveLike()
    give_like.start()
