# !/usr/bin/env python
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: config.py 
@time: 1/14/19 22:20 
@description：当当的配置文件
"""

import os

PLATFORM = 'Android'
DEVICE_NAME = 'MI_4LTE'
DRIVER = 'automationName2'
APP_PACKAGE = 'com.dangdang.buy2'
APP_ACTIVITY = 'com.dangdang.buy2.StartupActivity'
ANDROID_VERSION = '6.0.1'
AUTO_GRANT_PERMISSIONS = True

# ========================================

# Appium地址
DRIVER_SERVER = 'http://localhost:4723/wd/hub'

# 等待元素加载时间
TIMEOUT = 60

# 当当手机号码+密码
USERNAME = '13418516930'
PASSWORD = 'Hu881025'

# MongoDB配置【本地、数据库、集合表】
MONGO_URL = 'localhost'
MONGO_DB = 'admin'
MONGO_COLLECTION = 'moments'

# ===========================================

# 滑动点
# 起始点的x轴坐标、y轴坐标
FLICK_START_X = 300
FLICK_START_Y = 278

# 每次滑动的距离
FLICK_DISTANCE = 700

# 每次滑动的时间间隔
SCROLL_SLEEP_TIME = 1

# 搜索关键字
KEY_WORD = 'Python'
