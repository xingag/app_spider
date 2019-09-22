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
@time: 2019-09-20 11:24
@description：拿到手机号码后，获取全名
"""

import requests
from lxml import etree
from utils.device_utils import *
from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco

# 中国姓氏百度百科
family_name_url = 'https://baike.baidu.com/item/%E4%B8%AD%E5%9B%BD%E5%A7%93%E6%B0%8F/10514913'

headers = {
    'Connection': 'keep-alive',
    'Pragma': 'no-cache',
    'Cache-Control': 'no-cache',
    'Upgrade-Insecure-Requests': '1',
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36',
    'Sec-Fetch-Mode': 'navigate',
    'Sec-Fetch-User': '?1',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3',
    'Sec-Fetch-Site': 'none',
    'Accept-Encoding': 'gzip, deflate, br',
    'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
}


class GetUserName(object):
    def __init__(self, account):
        # 支付宝账号
        self.account = account

        # 名
        self.first_name = ''

        self.package_name_aliply = 'com.eg.android.AlipayGphone'

        self.target_activity_name = '.AlipayLogin'

        self.poco = AndroidUiautomationPoco(screenshot_each_action=False)
        auto_setup(__file__)

    def run(self):
        # 1、获取常用的姓氏
        last_names = self.__get_common_family_names()

        print('常用的姓氏如下：')
        print(last_names)

        # 2、打开支付宝转账界面
        self.__open_app()

        # 3、一个个姓氏去匹配
        find_index = -1
        current_last_name = ''

        for index, last_name in enumerate(last_names):
            print(f'判断姓氏:{last_name}')
            current_last_name = last_name

            # 4.模拟转账，并开始验证
            self.__simulate_transfer(last_name)

            # 5、判断姓氏是否输入正确
            yes_or_right = self.__judge_family_name()

            if yes_or_right:
                find_index = index
                break
            else:
                pass

        if find_index != -1:
            print(f'恭喜！查找到了~')
            print(f'暗恋女孩的全名是：{current_last_name}{self.first_name}')
        else:
            print('这个女孩大概来自于火星，姓氏暂时在地球上不存在！')

    def __get_avai_name(self, name):
        return len(name) == 1

    def __get_common_family_names(self):
        """
        爬取常用的姓氏
        :return:
        """
        resp_text = requests.get(family_name_url, headers=headers).content

        # print(resp_text)

        htmlElement = etree.HTML(text=resp_text)

        # 500多个常见姓氏
        names_pre = htmlElement.xpath("//table[@log-set-param='table_view']//tr//td/a/text()")

        # 过滤复姓
        names = list(filter(self.__get_avai_name, names_pre))

        print(f'常见姓氏：{len(names)}种')

        return names

    def __open_app(self):
        """
        打开转账界面
        :return:
        """
        home()
        print('打开支付宝')
        stop_app(self.package_name_aliply)
        start_my_app(self.package_name_aliply, self.target_activity_name)

        # 转账
        self.poco('com.alipay.android.phone.openplatform:id/app_text', text=u'转账').click()

        # 转账到支付宝
        self.poco('com.alipay.mobile.transferapp:id/to_account_view_tv', text=u'转到支付宝').click()

        # 输入账号
        # 输入账号
        self.poco('com.alipay.mobile.antui:id/input_edit').set_text(self.account)

        # 点击下一步
        self.poco('com.alipay.mobile.transferapp:id/tf_toAccountNextBtn').click()

    def __simulate_transfer(self, last_name):
        """
        模拟转账
        :return:
        """
        # 如果不是好友，就不会显示全名
        # 点击验证名称
        verify_element = self.poco('com.alipay.mobile.transferapp:id/tf_receiveNameTextView')
        verify_element.click()

        # 姓名除去姓氏
        # 遥（*遥）[点此验证]
        first_name_pre = verify_element.get_text()

        # 获取真实的first name
        self.first_name = first_name_pre[:first_name_pre.index('（')]

        # 获取姓氏输入框
        input_element = self.poco('com.alipay.mobile.antui:id/dialog_custom_view').parent().children()[1].children()[0]

        input_element.set_text(last_name)

        # 点击确认按钮，开始验证
        self.poco('com.alipay.mobile.antui:id/ensure').click()

    def __judge_family_name(self):
        """
        判断姓氏输入是否正确
        :return:
        """
        msg_error = self.poco('com.alipay.mobile.antui:id/message', text=u'姓名和账户不匹配，为避免转错账，请核对')
        btn_ensure = self.poco('com.alipay.mobile.antui:id/ensure')

        yes_or_right = False

        # 姓氏不对
        if msg_error.exists():
            print('姓氏输入不正确')
            btn_ensure.click()
            yes_or_right = False
        else:
            print('姓氏输入正确')
            yes_or_right = True

        return yes_or_right


if __name__ == '__main__':
    # 对方支付宝账号
    account = '***********'

    get_user_name = GetUserName(account)

    get_user_name.run()
