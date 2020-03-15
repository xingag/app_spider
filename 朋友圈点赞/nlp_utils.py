#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: nlp_utils.py 
@time: 2020-03-13 10:13 
@description：情感分析
"""

# 参考：https://ai.baidu.com/ai-doc/NLP/tk6z52b9z

# 依赖：pip3 install baidu-aip


from aip import AipNlp


def get_word_nlp(word):
    """
    是否为消极的
    :param word:
    :return:
    """
    """ 你的 APPID AK SK """
    APP_ID = '**'
    API_KEY = '***'
    SECRET_KEY = '****'

    client = AipNlp(APP_ID, API_KEY, SECRET_KEY)

    """ 调用情感倾向分析 """
    result = client.sentimentClassify(word)

    # 该情感搭配的极性（0表示消极，1表示中性，2表示积极）
    sentiment = result.get("items")[0].get("sentiment")

    return sentiment == 0
