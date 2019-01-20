#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: spider_dangdang.py 
@time: 1/18/19 12:22 
@description：使用appium爬取当当网的数据
"""

from config_dangdang import *
from appium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException, TimeoutException
import time
from element_utils import *
import threading


class DangDang(object):
    def __init__(self):
        self.caps = {
            'automationName': DRIVER,
            'platformName': PLATFORM,
            'deviceName': DEVICE_NAME,
            'appPackage': APP_PACKAGE,
            'appActivity': APP_ACTIVITY,
            'platformVersion': ANDROID_VERSION,
            'autoGrantPermissions': AUTO_GRANT_PERMISSIONS,
            'unicodeKeyboard': True,
            'resetKeyboard': True
        }
        self.driver = webdriver.Remote(DRIVER_SERVER, self.caps)
        self.wait = WebDriverWait(self.driver, TIMEOUT)


    def run(self, extra_job):
        time.sleep(5)

        # 1.搜索框
        search_element_pro = self.wait.until(
            EC.presence_of_element_located((By.ID, 'com.dangdang.buy2:id/index_search')))
        search_element_pro.click()

        search_input_element = self.wait.until(
            EC.presence_of_element_located((By.ID, 'com.dangdang.buy2:id/search_text_layout')))
        search_input_element.set_text(KEY_WORD)

        # 2.搜索对话框，开始检索
        search_btn_element = self.wait.until(
            EC.element_to_be_clickable((By.ID, 'com.dangdang.buy2:id/search_btn_search')))
        search_btn_element.click()

        # 3.休眠3秒，等待数据加载完成
        time.sleep(3)

        while True:
            str1 = self.driver.page_source
            self.driver.swipe(FLICK_START_X, FLICK_START_Y + FLICK_DISTANCE, FLICK_START_X, FLICK_START_X)
            time.sleep(1)
            str2 = self.driver.page_source
            if str1 == str2:
                print('停止滑动')
                # 停止线程
                extra_job.stop()
                break
            print('继续滑动')


class ExtraJob(threading.Thread):

    def __init__(self, driver):
        threading.Thread.__init__(self)

        # 用于暂停线程的标识
        self.__flag = threading.Event()
        # 设置为True
        self.__flag.set()

        # 用于停止线程的标识
        self.__running = threading.Event()
        # 将running设置为True
        self.__running.set()

        self.driver = driver

    def run(self):
        while self.__running.isSet():

            # 为True时立即返回, 为False时阻塞直到内部的标识位为True后返回
            self.__flag.wait()

            print('线程循环执行')

            # 1.0 【红包雨】对话框
            red_packet_element = is_element_exist(self.driver, 'com.dangdang.buy2:id/close')
            if red_packet_element:
                red_packet_element.click()

            # 1.1 【新人专享券】对话框
            new_welcome_page_sure_element = is_element_exist(self.driver, 'com.dangdang.buy2:id/dialog_cancel_tv')
            if new_welcome_page_sure_element:
                new_welcome_page_sure_element.click()

            # 1.2 【切换位置】对话框
            change_city_cancle_element = is_element_exist(self.driver, 'com.dangdang.buy2:id/left_bt')
            if change_city_cancle_element:
                change_city_cancle_element.click()

    def pause(self):
        self.__flag.clear()

    def resume(self):
        self.__flag.set()

    def stop(self):
        self.__flag.set()
        self.__running.clear()


if __name__ == '__main__':
    dangdang = DangDang()

    # 管理一些干扰界面
    extra_job = ExtraJob(dangdang.driver)
    extra_job.start()

    dangdang.run(extra_job)
