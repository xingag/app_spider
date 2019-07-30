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
@time: 2019-07-12 23:08 
@description：清理微信僵尸粉
"""
from airtest.core.android import Android

from file_utils import *
from airtest.core.api import *
from poco.exceptions import PocoNoSuchNodeException
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from device_utils import *
import logging

class Wx_Zombie_Fans(object):

    def __init__(self):
        self.package_name = 'com.tencent.mm'
        self.home_activity = 'com.tencent.mm.ui.LauncherUI'

        self.poco = None

        # 定义的元素ID
        # 首页，搜索图标按钮
        self.id_search = 'com.tencent.mm:id/qh'
        # 搜索输入框
        self.id_search_input = 'com.tencent.mm:id/lh'
        # 搜索结果列表
        self.id_search_result_list = 'com.tencent.mm:id/c12'
        # 搜索联系人标识
        self.text_contact_tips = '联系人'
        # 聊天界面，更多
        self.id_chat_more_button = 'com.tencent.mm:id/aoe'
        # 容器
        self.id_chat_more_container = 'com.tencent.mm:id/aog'

        # 转账文本
        self.text_chat_transfer_account_text = '转账'

        # 转账输入框
        self.id_transfer_account_input = 'com.tencent.mm:id/d8'

        # 转账容器
        self.id_transfer_account_container = 'com.tencent.mm:id/fsa'

        # 转账结果Tips
        self.id_transfer_account_result_tips = 'com.tencent.mm:id/dcf'

        # 不是好友的提示
        self.text_friend_no_tips = '你不是收款方好友，对方添加你为好友后才能发起转账'

        # 好友限制登录的提示
        self.text_friend_limit_tips = '对方账号处于限制登录状态，暂不能接收转账'

        # 好友关系是否正常
        self.text_friend_is_norm = '请确认你和他（她）的好友关系是否正常'

        # 给好友转账的异常对话框的【确定】按钮
        self.id_transfer_account_result_sure_button = 'com.tencent.mm:id/b1v'
        self.text_transfer_account_result_sure_button = '确定'

        # 个人资料按钮
        self.id_person_msg_button = 'com.tencent.mm:id/kz'

        # 个人资料的头像
        self.id_person_head_url = 'com.tencent.mm:id/e9w'

        # 好友操作菜单
        self.id_person_manage_menu = 'com.tencent.mm:id/kz'

        # 删除好友
        self.id_person_del = 'com.tencent.mm:id/cz'
        self.text_person_del = '删除'

        # 删除好友对话框里的【删除】确定键
        self.id_person_del_sure = 'com.tencent.mm:id/b1v'

        # 黑名单列表
        self.friend_black_list = []
        self.path_black_list = './data/black_list.txt'

        # 关系异常好友数据保存路径
        self.path_relationship_unnormal = './data/unnorm_relationship_friend.txt'

        # 对方账号受限
        self.path_account_limit = './data/account_limit.txt'

        # 固定转账的金额1分钱【小额度】
        self.money = '0.01'

    def run(self):
        """
        清理僵尸粉的步骤
        :return:
        """
        # 1、初始化Airtest
        self.__init_airtest()

        # 1.1、日志
        self.__init_log()

        # 2.1、导出数据
        # export_wx_db_from_phone('./data/wx_data.csv')
        # sleep(5)

        # 2.2、读取微信通讯录
        # 直接破解微信数据库，筛选出所有的微信好友
        friends = read_csv('./data/wx_data.csv')

        # 3、打开微信
        self.open_weixin()

        # 4、循环去打个每一个好友的聊天界面，尝试给对方转账
        for friend in friends:
            friend_id = friend.get('alias')
            friend_name = friend.get('nickName')

            # 注意：很多微信好友没有微信号，这时候可以使用昵称进行搜索
            search_id = friend_id if friend_id else friend_name

            print('现在检查【%s】是否是自己的好友' % friend_name)

            # 打开好友的聊天界面
            self.__to_friend_chat_page(search_id)

            # 判断双方是否为好友，或者是已经被对对方拉黑
            self.__judge_is_friend(search_id, friend_name)

        # 5.哪些好友把自己拉黑了
        print('==' * 60)
        print('下面这些人，把你拉黑了：')
        for friend_black in self.friend_black_list:
            print(friend_black.get('nickName'))
        print('==' * 60)

        # 6、处理这些黑名单好友
        input_msg = input('是否删除这些曾今的好友？(y/n)')
        if 'y' == input_msg:
            # 循环去删除这些黑名单好友
            for friend_black in self.friend_black_list:
                self.del_friend_black(friend_black.get('id'))
                print('删除好友：【%s】成功' % friend_black.get('nickName'))

            print('恭喜！你的通讯录内都是纯正的朋友~')

    def open_weixin(self):
        """
        打开微信
        :return:
        """
        # 清空文件
        clean_file(self.path_black_list)
        clean_file(self.path_relationship_unnormal)
        clean_file(self.path_account_limit)

        # 打开微信
        home()
        stop_app(package=self.package_name)
        start_my_app(package_name=self.package_name, activity_name=self.home_activity)

    def del_friend_black(self, weixin_id):
        """
        删除黑名单好友
        :return:
        """
        # 到好友聊天界面
        self.__to_friend_chat_page(weixin_id)

        # 点击聊天界面右上角，进入到好友的详细信息界面
        self.poco(self.id_person_msg_button).click()

        # 点击好友头像
        self.poco(self.id_person_head_url).click()

        # 点击个人名片的右上角,弹出好友操作菜单
        self.poco(self.id_person_manage_menu).click()

        # 查找删除操作栏
        # 注意：对于目前主流的手机，都需要滑动到最底部才能出现【删除】这一操作栏
        self.poco.swipe([0.5, 0.9], [0.5, 0.3], duration=0.2)

        # 点击删除，弹出删除对话框
        self.poco(self.id_person_del, text=self.text_person_del).click()

        # 确定删除好友【确定删除】
        # 界面会直接回到主界面
        self.poco(self.id_person_del_sure, text=self.text_person_del).click()

    def __to_friend_chat_page(self, weixin_id):
        """
        点击到一个好友的聊天界面
        :param weixin_id:
        :param weixin_name:
        :return:
        """
        # 1、点击搜索
        element_search = self.__wait_for_element_exists(self.id_search)
        element_search.click()

        print('点击搜索')

        # 2、搜索框
        element_search_input = self.__wait_for_element_exists(self.id_search_input)
        element_search_input.set_text(weixin_id)

        # 3、搜索列表
        element_search_result_list = self.__wait_for_element_exists(self.id_search_result_list)

        # 3.1 是否存在对应的联系人，如果存在就在第一个子View布局下
        # 注意：可能出现最常用的聊天列表，这里需要进行判断
        index_tips = 0
        for index, element_search_result in enumerate(element_search_result_list.children()):
            # 联系人的Tips
            # if element_search_result_list.children()[0].offspring(self.id_contact_tips).exists():

            if element_search_result.offspring(text=self.text_contact_tips).exists():
                index_tips = index
                break

        # 4、点击第一个联系人进入聊天界面
        element_search_result_list.children()[index_tips + 1].click()

    def __judge_is_friend(self, weixin_id, weixin_name):
        """
        判断是不是微信好友
        :param weixin_id: 微信号
        :return:
        """
        # 尝试给好友转账，设置一个小额度，以防止刷脸直接支付了
        # 如果对方是你的好友，接下来会让你输入密码，关掉页面就行了
        # 如果对方不是你的好友，会提示不是你的好友，不能继续操作了
        # 5、点击好友界面的+按钮
        self.poco(self.id_chat_more_button).click()

        # 6、点击转账按钮
        self.poco(self.id_chat_more_container).offspring(text=self.text_chat_transfer_account_text).click()

        # 7、输入金额
        self.poco(self.id_transfer_account_input).set_text(self.money)

        # 8、点击转账按钮
        self.poco(self.id_transfer_account_container).offspring(text=self.text_chat_transfer_account_text).click()

        # 9.判断是否是好友
        element_transfer_account_result_button = self.poco(self.id_transfer_account_result_sure_button,
                                                           text=self.text_transfer_account_result_sure_button)

        # 10.弹出警告对话框
        # 弹出好友关系不正常
        if element_transfer_account_result_button:
            # 提示内容
            transfer_account_result_tips = self.poco(self.id_transfer_account_result_tips).get_text()

            if self.text_friend_no_tips in transfer_account_result_tips:
                print('注意！%s已经把你拉黑了!!!' % weixin_name)
                self.friend_black_list.append({
                    'id': weixin_id,
                    'nickName': weixin_name
                })
                write_to_file(self.path_black_list, 'id:%s,nickName:%s' % (weixin_id, weixin_name))
            elif self.text_friend_limit_tips in transfer_account_result_tips:
                print('%s账号收到限制!!!' % weixin_name)
                write_to_file(self.path_account_limit, 'id:%s,nickName:%s' % (weixin_id, weixin_name))
            elif self.text_friend_is_norm in transfer_account_result_tips:
                print('%s好友关系不正常!!!' % weixin_name)
                write_to_file(self.path_relationship_unnormal, 'id:%s,nickName:%s' % (weixin_id, weixin_name))

            # 点击确认按钮
            element_transfer_account_result_button.click()

            # 返回到主页面
            self.__back_to_home()

        else:
            # 包含正常好友关系和对方账号限制的情况
            print('好友关系正常')
            self.__back_to_home()

    def __back_to_home(self):
        """
        回退到主界面
        :return:
        """
        print('准备回退到主界面')
        home_tips = ['微信', '通讯录', '发现', '我']
        while True:
            keyevent('BACK')
            is_home = False

            # 判断是否到达首页
            if self.poco(text=home_tips[0]).exists() and self.poco(text=home_tips[1]).exists() and self.poco(
                    text=home_tips[2]).exists() and self.poco(text=home_tips[3]).exists():
                is_home = True

            if is_home:
                print('已经回到微信首页~')
                break

    def __wait_for_element_exists(self, element_id):
        """
        一直等待元素出现
        :param elements: 元素列表
        :return:
        """
        while True:
            try:
                # 元素是否存在
                element_exists = True

                elements = self.poco(element_id)

                # 元素列表
                for element in elements:
                    if not element.exists():
                        element_exists = False
                        break
                    else:
                        continue

                if element_exists:
                    # print('找到元素了，退出！')
                    return elements
                else:
                    # print('界面元素暂时找不到，继续等待')
                    continue
            except PocoNoSuchNodeException as e:
                pass
                # print('界面查找元素异常~')

    def __init_airtest(self):
        """
        初始化Airtest
        :return:
        """
        device_1 = Android('822QEDTL225T7')
        # device_1 = Android('emulator-5554')

        connect_device("android:///")

        self.poco = AndroidUiautomationPoco(device_1, screenshot_each_action=False)

        auto_setup(__file__)

    def __init_log(self):
        """
        日志
        :return:
        """
        pass
        # logging.basicConfig(level=print,
        #                     filename='wx.log',
        #                     filemode='a',  # 模式，有w和a，w就是写模式，每次都会重新写日志，覆盖之前的日志
        #                     # a是追加模式，默认如果不写的话，就是追加模式
        #                     format=
        #                     '%(asctime)s - %(pathname)s[line:%(lineno)d] - %(levelname)s: %(message)s'
        #                     # 日志格式
        #                     )


if __name__ == '__main__':
    wx_zombie_fans = Wx_Zombie_Fans()

    wx_zombie_fans.run()
