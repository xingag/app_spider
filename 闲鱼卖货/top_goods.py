#!/usr/bin/env python
# encoding: utf-8

"""
@version: v1.0
@author: xag
@license: Apache Licence
@contact: xinganguo@gmail.com
@site: http://www.xingag.top
@software: PyCharm
@file: top_goods.py
@time: 2019-10-02 11:16
@description：自动化爬虫销量最高的10条数据
"""

__author__ = "xingag"

from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from utils.device_utils import *
from utils.file_util import *
from utils.element_util import *
from utils.xianyu_util import *
import time
import yaml
import traceback
import datetime
import operator
from pyecharts.charts import Bar
from pyecharts import options as opts

package_name = 'com.taobao.idlefish'
activity = 'com.taobao.fleamarket.home.activity.InitActivity'


class GoodTop(object):
    def __init__(self):

        good_opts = self.__get_yaml('_goods.yaml')

        # 要爬取的商品
        self.good = good_opts.get('goods').get('good1')

        # 筛选【想要数】多少的商品
        self.num_assign = self.good.get('key_num')

        # 筛选数目
        self.num = self.good.get('num')

        # 查询关键字
        self.good_msg = self.good.get('key_word')

        # 文件路径,以时间+关键字组成
        self.file_path = './%s_%s.csv' % (self.good_msg, datetime.datetime.now().strftime("%Y_%m_%d"))

        # 爬取时间
        self.spider_time = int(self.good.get('time'))

        # 爬取的数据
        self.good_results = []

        # 已经处理的商品
        self.title_handled = []

        self.poco = AndroidUiautomationPoco(screenshot_each_action=False)
        auto_setup(__file__)

    def __get_yaml(self, yaml_file):
        """
        解析 yaml 配置文件
        :return: s  字典
        """
        path = os.path.join(os.path.dirname(__file__), yaml_file)
        print(path)
        try:
            with open(path, 'r', encoding='utf-8') as file:
                config = yaml.safe_load(file)
            return config
        except Exception as exception:
            print(str(exception))
            print('你的 _config.yaml 文件配置出错...')
        return None

    def run(self):
        # 1、打开闲鱼客户端
        self.__pre()

        # 2、输入关键字，到达商品列表界面
        self.__input_key_word()

        # 4、获取每次滑动最合适的距离
        good_distance = self.__get_good_swipe_distance()

        print(f'最佳滑动距离:{good_distance}')

        # 从这里开始计时
        browser_start = datetime.datetime.now()
        browser_end = browser_start

        # 5、循环查找
        while (browser_end - browser_start).seconds < self.spider_time:
            try:
                self.__handle_good_list()
            except:
                pass

            exec_cmd('adb shell input swipe %d %d %d %d %d' % (500, 1800, 500, 1800 - good_distance, 2000))

            print(get_remain_time(self.spider_time - (browser_end - browser_start).seconds))

            # 结束时间
            browser_end = datetime.datetime.now()

        # 6、对结果进行排序
        sortedlist = self.__sort_result()

        # 7、对结果进行图片展示
        self.draw_image(sortedlist)

    def __pre(self):
        """
        准备工作
        :return:
        """
        home()
        stop_app(package_name)
        start_my_app(package_name, activity)

        # 删除文件并新建，写入头部
        if os.path.exists(self.file_path):
            os.remove(self.file_path)

        write_to_csv(self.file_path, None, True)

        # 如果20s内有淘口令，就关闭
        for i in range(10, -1, -1):
            close_element = self.poco('com.taobao.idlefish:id/ivClose')
            if close_element.exists():
                close_element.click()
                break
            time.sleep(1)

        # 等待到达桌面
        self.poco(text='闲鱼').wait_for_appearance()
        self.poco(text='鱼塘').wait_for_appearance()
        self.poco(text='消息').wait_for_appearance()
        self.poco(text='我的').wait_for_appearance()

        print('进入闲鱼主界面')

    def __input_key_word(self):
        """
        输入关键字
        :return:
        """
        # 进入搜索界面
        perform_click(self.poco('com.taobao.idlefish:id/bar_tx'))

        # 搜索框内输入文本
        self.poco('com.taobao.idlefish:id/search_term').set_text(self.good_msg)

        # 点击搜索按钮
        while True:
            # 等待检索结果列表出现
            if not self.poco('com.taobao.idlefish:id/list_recyclerview').exists():
                perform_click(self.poco('com.taobao.idlefish:id/search_button', text='搜索'))
            else:
                break

        # 等待商品列表完全出现
        self.poco('com.taobao.idlefish:id/list_recyclerview').wait_for_appearance()

        # 切换到列表
        perform_click(self.poco('com.taobao.idlefish:id/switch_search'))

    def __handle_good_list(self):
        """
        处理商品列表
        :return:
        """

        elements = self.poco('com.taobao.idlefish:id/root_view')

        print(f'子元素个数：{len(elements)}')

        for item in elements:

            try:
                title_element = item.offspring('com.taobao.idlefish:id/title_img')
            except Exception as e:
                print('获取标题产生异常')
                continue

            if not title_element:
                print('标题不存在')
            else:
                # 标题内容，截取前 20 位数字
                title = cut_title(title_element.get_text(), 20)

                print(f'标题是：{title}')

                # 避免重复处理
                if title in self.title_handled:
                    continue
                else:
                    self.title_handled.append(title)

                # 多少人想要
                want_element_parent = item.offspring('com.taobao.idlefish:id/search_item_flowlayout')
                if want_element_parent.exists():

                    # 想要数/已付款数目
                    want_element = want_element_parent.children()[0]

                    want_content = want_element.get_text()

                    # 过滤掉【已付款】等其他商品，只保留个人发布商品
                    if '人想要' not in want_content:
                        continue

                    # 拿到商品想要的具体数目，代表商品热度
                    want_num = get_num(want_content)

                    if int(want_num) < self.num_assign:
                        # print('不达标，过滤掉')
                        pass
                    else:
                        print('达标，获取分享链接')
                        perform_click(item)
                        share_url = self.__get_good_share_url()

                        # 写入到文件中
                        write_to_csv(self.file_path, [(title, want_num, share_url)], False)
                else:
                    # 两种情况：这个Item没人要、内容暂时没有展示出来
                    pass

    def __get_good_share_url(self):
        """
        获取商品的分享链接
        :return:
        """
        # 点击更多
        while True:
            if self.poco('com.taobao.idlefish:id/ftShareName').exists():
                break
            print('点击更多~')
            perform_click(self.poco(text='更多'))

        # 点击复制淘口令
        perform_click(self.poco('com.taobao.idlefish:id/ftShareName', text='淘口令'))

        # 拿到口令码
        taobao_code_element = self.poco('com.taobao.idlefish:id/tvWarnDetail')

        taobao_code = taobao_code_element.get_text()

        # 返回到主界面
        print('返回到列表界面')
        perform_back(self.poco, 'com.taobao.idlefish:id/list_recyclerview')

        return taobao_code

    def __get_good_swipe_distance(self):
        """
        获取每次滑动，最合适的距离
        :return:
        """
        element = Element()
        # 保存当前的UI树到本地
        element.get_current_ui_tree()

        # 第一个商品Item的坐标
        position_item = element.find_elment_position_by_id_and_index("com.taobao.idlefish:id/card_root",
                                                                     "1")
        # 商品的高度
        item_height = position_item[1][1] - position_item[0][1]

        # 通过观察，当前屏幕有3件商品
        return item_height * 3

    def __sort_result(self):
        """
        对爬取的结果进行排序
        :return:
        """
        reader = csv.reader(open(self.file_path), delimiter=",")

        # 头部标题
        head_title = next(reader)

        # 按照第二列进行逆序排列
        sortedlist = sorted(reader, key=lambda x: (int(x[1])), reverse=True)

        # 删除文件并重新写入排序后的数据
        if os.path.exists(self.file_path):
            os.remove(self.file_path)

        # 写入头部数据
        write_to_csv(self.file_path, [(head_title[0], head_title[1], head_title[2])], False)

        for value in sortedlist:
            write_to_csv(self.file_path, [(value[0], value[1], value[2])], False)

        return sortedlist

    def draw_image(self, sortedlist):
        """
        画图
        :param sortedlist:
        :return:
        """

        # 标题列表
        titles = []

        # 销量
        sales_num = []

        # 拿到爬取结果的标题、销量两个列表
        with open(self.file_path, 'r') as csvfile:
            # 读取文件
            reader = csv.DictReader(csvfile)

            # 加入列表中
            for row in reader:
                titles.append(row['title'])
                sales_num.append(row['num'])

        # 数目限制
        if len(titles) > self.num:
            titles = titles[:self.num]
            sales_num = sales_num[:self.num]

        # 画图
        bar = (
            Bar()
                .add_xaxis(titles)
                .add_yaxis("哪些好卖", sales_num)
                .set_global_opts(title_opts=opts.TitleOpts(title="我要卖货"))
        )
        bar.render('%s.html' % self.good_msg)


if __name__ == '__main__':
    good_top = GoodTop()

    good_top.run()

    print('今天爬取数据完成！')
