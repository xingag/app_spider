#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: news.py 
@time: 2020-05-06 12:43 
@description：欢迎关注公众号：AirPython
"""

# 依赖
# pip3 install fastapi
# pip3 install uvicorn
# pip3 install hypercorn


# -*- coding: utf-8 -*-
from fastapi import FastAPI

app = FastAPI()


@app.get("/")
def read_root():
    return {"Hello": "World"}


@app.get("/last_news")
def get_last_news():
    """
    最新的新闻
    :return:
    """
    # 获取新闻（爬虫）
    news = get_news()

    data = {
        'code': 0,
        'news': news
    }

    # 封装
    return data

# 运行服务
# uvicorn news:app --reload

# 后台一直运行
# nohup hypercorn news:app --bind 0.0.0.0:8000 > /news.log 2>&1 &
