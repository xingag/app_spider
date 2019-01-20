#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: script_dangdang.py 
@time: 1/18/19 20:25 
@description：使用mitmproxy解析当当网的数据，并保存到MongoDB数据库
"""

from pymongo import MongoClient
from config_dangdang import *
from urllib.parse import unquote
import json


class DangDangMongo(object):
    """
    初始化MongoDB数据库
    """

    def __init__(self):
        self.client = MongoClient('localhost')
        self.db = self.client['admin']
        self.db.authenticate("root", "xag")
        self.dangdang_book_collection = self.db['dangdang_book']


def response(flow):
    request = flow.request
    response = flow.response

    # 过滤请求的URL
    if 'keyword=Python' in request.url:

        data = json.loads(response.text.encode('utf-8'))

        # 书籍
        products = data.get('products') or None

        product_datas = []

        for product in products:
            # 书ID
            product_id = product.get('id')

            # 书名
            product_name = product.get('name')

            # 书价格
            product_price = product.get('price')

            # 作者
            authorname = product.get('authorname')

            # 出版社
            publisher = product.get('publisher')

            product_datas.append({
                'product_id': product_id,
                'product_name': product_name,
                'product_price': product_price,
                'authorname': authorname,
                'publisher': publisher
            })

        DangDangMongo().dangdang_book_collection.insert_many(product_datas)
        print('成功插入数据成功')
