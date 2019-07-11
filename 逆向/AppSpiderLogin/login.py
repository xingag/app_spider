#!/usr/bin/env python  
# encoding: utf-8  

"""
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: login.py 
@time: 2019-06-25 09:48 
@description：破解两个Token参数，实现登录某App
"""
import requests
from utils import *
import datetime
from des_utils import *
from md5_utils import *
import re
from urllib.parse import urlencode
from date_utils import *
import json
from stations import stations
import time

from enum import Enum


# 请求的类型
class Method(Enum):
    POST = 1
    GET = 2


# 抢票今天还是明天,其他指定的日期
class DAY(Enum):
    TODAY = 1
    TOMORROW = 2
    OTHER = 3


# 注意：token的时效性，和请求的时间保持一致
# 两种Host对应两个Headers
HEADERS = {
    'Host': '**',
    'Accept': '*/*',
    'Version': '6.1',
    'User-Agent': '**/6.1 (iPhone; iOS 12.3.1; Scale/2.00)',
    'Accept-Language': 'zh-Hans-HK;q=1, zh-Hant-HK;q=0.9, en-HK;q=0.8',
    'Accept-Encoding': 'gzip, deflate',
    'token': 'b5d796a394bc7e7694a90e264cc6c6d2',
    'Connection': 'keep-alive'
}

HEADERS2 = {
    'Host': '**',
    'Accept': '*/*',
    'Version': '6.1',
    'User-Agent': '**/6.1 (iPhone; iOS 12.3.1; Scale/2.00)',
    'Accept-Language': 'zh-Hans-HK;q=1, zh-Hant-HK;q=0.9, en-HK;q=0.8',
    'Accept-Encoding': 'gzip, deflate',
    'token': 'b5d796a394bc7e7694a90e264cc6c6d2',
    'Connection': 'keep-alive'
}


class Bus(object):
    def __init__(self):
        # 手机号码
        self.phone = '11位手机号码'

        # 抢票：今天或者明天
        self.ticket_get = DAY.OTHER

        # 指定具体的日期
        self.ticket_get_special = '2019-07-10'

        # 车次
        self.bus_no = '车次'

        # 查询车次的路线id【固定】
        self.lineId = '76954'

        # vehTime:与车次绑定【固定】
        self.vehTime = '1745'

        # 上车地点：**
        self.onStationId = stations.get('xm1')

        # 下车地点：**
        self.offStatinId = stations.get('jwh2')

        # 用户ID【登录后获取】
        self.customerId = '582933'

        # keycode【登录后获取】
        self.keyCode = '5a9312694fba15ed36c6e46df4a63b1e'

        # 获取验证码
        self.code_url = 'http://**/code/phone/login/new?phone={}&t={}&token={}'

        # 登录
        self.login_url = 'http://**/phone/login/new'

        # 获取车次列表
        self.get_bus_url = 'http://**/bc/phone/data?lineNo={}&pageNo=1&pageSize=5&t={}'

        # 查询余票
        # customerId:登录后获取的
        self.search_left_ticket_url = 'http://**/bc/phone/surplus/ticket/new?beginDate={}&customerId={}&customerName={}&endDate={}&keyCode={}&lineId={}&t={}&vehTime={}'

        # 买票
        self.buy_ticket_url = 'http://**/order/phone/create?keyCode={}&lineId={}&offStationId={}&onStationId={}&payType=3&saleDates={}&startTime={}&t={}&tradePrice=10&userId={}&userName={}&vehTime={}'

        self.param_token = ''

        self.head_token = ''

        self.login_params = {
            'loginCode': '4068',
            'loginName': self.phone,
            't': '1561427542527'
        }

    def get_code(self, timestamp):
        """
        获取验证码
        :return:
        """

        # 1.1 获取参数Token,与日期有关
        self.param_token = self.__get_param_token(self.phone)
        print("parm_token:" + self.param_token)

        # 1.2 获取请求头Token，与时间有关
        url = self.code_url.format(self.phone, timestamp, self.param_token)

        # 获取请求头中的Token
        self.head_token = self.__get_head_token(Method.GET, url, None)

        print('head_token【获取验证码】:' + self.head_token)

        # 2.获取手机验证码的URL
        get_code_url = self.code_url.format(self.phone, timestamp, self.param_token)

        # 3.修改Head中的token
        HEADERS['token'] = self.head_token

        print(get_code_url)

        # 4.发起【获取验证码】的请求
        resp = requests.get(get_code_url, headers=HEADERS)

        print('==' * 60)
        print(resp.text)

    def login(self, code, timestamp):
        """
        登录
        :return:
        """

        # 修改参数
        self.login_params['loginCode'] = code
        self.login_params['t'] = timestamp

        # 请求token
        # url = self.code_url.format(self.phone, timestamp, self.param_token)
        self.head_token = self.__get_head_token(Method.POST, None, self.login_params)

        print('head_token【登录】:' + self.head_token)

        HEADERS['token'] = self.head_token

        # 登录
        resp = requests.post(self.login_url, data=self.login_params, headers=HEADERS)

        print(resp.text)

    def __get_param_token(self, phone_num):
        """
        获取参数Token
        :return: BNpK8SMDiV6jTU4DR99A9vYoN9e90yBd
        """
        today = datetime.date.today()
        formatted_today = today.strftime('%Y%m%d')

        formatted_day = today.strftime('%m%d')

        # 参数1  11位手机号码|20190704
        arg1 = phone_num + "|" + formatted_today

        # 参数2  64230704
        # 字符串转为bytes
        arg2 = bytes(phone_num[7:] + formatted_day, encoding="utf8")

        # 通过md5+base64得到一个令牌Token
        token = encode(arg1, arg2).replace('+', '!', 1000)

        return token

    def __get_head_token(self, method, url, data):
        """
        获取请求头Token
         分为Get和Post请求方式
        :param method: 请求方式
        :param url: 请求URL
        :param data: Post请求中的参数
        :return:
        """
        today = datetime.date.today()
        formatted_today = today.strftime('%Y%m%d')

        if method == Method.GET:
            # 请求的URL的query部分
            query_content = url.split('?')[1]
        else:
            query_content = urlencode(data)

        print('query_content:' + query_content)

        # 根据反编译后的源码增加对应的逻辑
        token_pro = query_content + "|" + formatted_today + '|zxw'

        # MD5计算
        token = md5(token_pro)

        return token

    def start(self):
        # 0.当前系统时间戳
        timestamp = get_unix_time(True)

        print('时间戳：' + str(timestamp))

        # 1.获取验证码
        # self.get_code(timestamp)

        # 由于验证码是服务端生成的，所有手动输入
        # code = input('请输入验证码:')

        # 2.登录
        # self.login(code, timestamp)

        # 4 获取某个车次列表
        # self.get_buses(timestamp)

        # 5.查询余票
        # 今日和明天是否有票
        while True:
            ticket_left_result = self.search_left_ticket(timestamp)

            # 判断是否有票
            has_ticket = ticket_left_result[0] if self.ticket_get == DAY.TODAY else ticket_left_result[1]

            if has_ticket:
                print('有票了！')
                break
            else:
                print('没有余票！休息1s后继续刷票~')
                time.sleep(1)

        # 6.买票
        self.buy_ticket(timestamp)

    def get_buses(self, timestamp):
        """
        获取车次信息
        :param timestamp:
        :return:
        """
        url = self.get_bus_url.format(self.bus_no, timestamp)
        print(url)

        self.head_token = self.__get_head_token(Method.GET, url, None)

        print('head_token【车次列表】：' + self.head_token)

        HEADERS2['token'] = self.head_token

        # 发起【获取车次列表】的请求
        resp = requests.get(url, headers=HEADERS2)

        print(resp.text)

    def search_left_ticket(self, timestamp):
        """
        查询某个车次余票
        "tickets": "-1,-1,1,1,1,0,0,-1,-1,0,0,0,0,0,-1,-1,0,0,0,0,0,-1,-1,0,0,0"
        -1：周末，不容许选择；1：余票1；0：没票
        :return:
        """
        today = datetime.date.today()
        # 开始时间:今天
        start_day = today.strftime('%Y%m%d')

        # 结束时间：月底
        end_day = get_last_day()

        url = self.search_left_ticket_url.format(start_day, self.customerId, self.phone, end_day, self.keyCode,
                                                 self.lineId,
                                                 timestamp, self.vehTime)

        self.head_token = self.__get_head_token(Method.GET, url, None)

        print('head_token【查询余票】：' + self.head_token)

        HEADERS2['token'] = self.head_token

        # 发起【获取车次列表】的请求
        resp = requests.get(url, headers=HEADERS2)

        # 服务器返回的余票数据
        tickets_pro = json.loads(resp.text).get('returnData').get('tickets')

        tickets_str_list = tickets_pro.split(',')

        # 列表中的数据转为整形
        tickets_int_list = [int(ticket_item) for ticket_item in tickets_str_list]

        # 今日和明天是否有票
        return tickets_int_list[0] > 0, tickets_int_list[1] > 0

    def buy_ticket(self, timestamp):
        """
        购票
        :return:
        """
        today = datetime.date.today()
        tomorrow = today + datetime.timedelta(days=1)

        if self.ticket_get == DAY.TODAY:
            saleDates = today.strftime('%Y-%m-%d')
        elif self.ticket_get == DAY.TOMORROW:
            saleDates = tomorrow.strftime('%Y-%m-%d')
        else:
            saleDates = self.ticket_get_special

        # 购买票的URL
        url = self.buy_ticket_url.format(self.keyCode, self.lineId, self.offStatinId, self.onStationId, saleDates,
                                         "1756", timestamp, self.customerId, self.phone, self.vehTime)

        self.head_token = self.__get_head_token(Method.GET, url, None)

        HEADERS['token'] = self.head_token

        # 发起购买票的请求
        resp = requests.get(url, headers=HEADERS)

        print(resp.text)

        print('恭喜！买到一张票~')


if __name__ == '__main__':
    bus = Bus()

    bus.start()
