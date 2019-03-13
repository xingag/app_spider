#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: comments.py 
@time: 2/8/19 18:41 
@description：评论
"""

from random import choice


def generate_a_comment():
    comments = [
        '666',
        '看过',
        '路过',
        'en',
        '嗯',
        '空闲看看',
        '每天看一下',
        '每天必须看看',
        '我要币',
        '我要金币',
        '签到',
        '123',
        'hao',
        '好',
        '已读',
        'yidu',
        '已阅',
        '有点意思',
        '新闻知天下',
        '阅读',
        '随机',
        'meibanfa',
        '没办法',
        'm',
        'a',
        '好的好的',
        '牛',
        '牛逼',
        '这是什么地方',
        '哪',
        '哪里',
        '哪啊',
        '哪？'
    ]

    return choice(comments)
