#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: 2.firends_circle.py
@time: 2/1/19 11:26 
@description：爬取朋友圈
"""

# -*- encoding=utf8 -*-
__author__ = "xingag"

from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from utils.file_utils import *
from utils.string_utils import *
from utils.device_utils import *

poco = AndroidUiautomationPoco(use_airtest_input=True, screenshot_each_action=False)

auto_setup(__file__)

# 微信文件保存目录
WEI_XIN_FILE_PATH = '/storage/emulated/0/tencent/MicroMsg/WeiXin/'

# 应用包名和启动Activity
package_name = 'com.tencent.mm'
activity = 'com.tencent.mm.ui.LauncherUI'

# 1.打开微信
home()
stop_app(package_name)
start_my_app(package_name, activity)

# poco('微信').click()
sleep(5)

# 2.打开朋友圈
poco(text='发现').click()
poco(text='朋友圈').click()

# 3.休眠5秒，待第一页的数据加载完全
sleep(5)


# 4.获取个人信息
def get_user_info():
    username = poco(name="com.tencent.mm:id/b4o").get_text()
    # 4.1 点击头像
    poco(name="com.tencent.mm:id/pn").click()
    # 4.2 再次点击
    poco(name="com.tencent.mm:id/b2w").click()
    # 4.3 截取个人头像[整个屏幕]
    # snapshot(filename='./person_ic.jpg')

    # 4.4 长按图片
    # poco.long_click(name="")
    poco(name="android.widget.ImageView").long_click()

    # 4.5 保存图片到手机文件
    # 删除微信文件夹
    del_files(WEI_XIN_FILE_PATH)
    poco("com.tencent.mm:id/kh").child("android.widget.LinearLayout").child("android.widget.LinearLayout").click()

    # 4.6 从手机中复制图片到PC端
    copy_last_pic_to_local(WEI_XIN_FILE_PATH, 'person')


# get_user_info()

# 5.按Android手机返回到朋友圈主界面
# keyevent("BACK")
# keyevent("BACK")

# 5.1 点击Android手机的Home键
# keyevent("HOME")

# 6.爬取朋友圈动态
moods = []


def get_dynamic_moods():
    head_dynamic_moods = poco("com.tencent.mm:id/ebi").child(name='com.tencent.mm:id/efo')

    if not head_dynamic_moods.exists():
        return

    # 一条心情中，昵称、发布时间是肯定存在的两个数据
    for head_dynamic_mood in head_dynamic_moods:
        # 当前动态的高度
        height = head_dynamic_mood.get_size()[1]
        print('当前Item高度:' + str(height))

        # 昵称
        nickname_element = head_dynamic_mood.offspring('com.tencent.mm:id/b4o')
        # 动态
        dynamic_mood_element = head_dynamic_mood.offspring('com.tencent.mm:id/efs')

        # 发布时间
        # 注意：如果发布时间元素还没有拖动上来，这里可能就为空
        pub_time_element = head_dynamic_mood.offspring('com.tencent.mm:id/eay')

        # 广告标识
        advertising_tips_element = head_dynamic_mood.offspring('com.tencent.mm:id/e_f')

        # 图片【可以有多个】
        image_element = head_dynamic_mood.offspring('com.tencent.mm:id/efe')

        # 视频【只能有一个】
        video_element = head_dynamic_mood.offspring('com.tencent.mm:id/ao4')

        # 广告动态筛除掉、没有加载完全的元素筛除掉
        try:
            if advertising_tips_element.exists() or not pub_time_element.exists() or not nickname_element.exists():
                continue
        except Exception as e:
            print('筛选中产生一条异常')
            continue

        # 昵称可能有表情，这里要删除掉
        nickname = filter_emoji(nickname_element.get_text())
        print('昵称：%s' % nickname)

        # 附件保存目录
        adjunct_path = './adjunct/%s' % nickname

        msg = dynamic_mood_element.get_text() if dynamic_mood_element.exists() else '动态中没有文字'
        pub_time = pub_time_element.get_text()

        item = {
            'nickname': nickname,
            'msg': filter_emoji(msg),
            'pub_time': pub_time
        }

        if pub_time == '昨天':
            print('这是一条昨天的动态，停止爬取。。。')
            return False

        # 这里要判断是否重复插入
        if item not in moods:

            # 下载图片，如果存在
            if image_element.exists():
                image_children = image_element.child()
                image_size = len(image_children)
                print('共有%d张图片' % image_size)

                # 每一张图片单独点击保存
                for index, image_child in enumerate(image_children):
                    print('现在操作第%d张图片' % index)
                    if index == 0:
                        image_child.click()

                    del_files(WEI_XIN_FILE_PATH)
                    poco("android.widget.LinearLayout").offspring('com.tencent.mm:id/j7').long_click()
                    poco(text="保存图片").click()
                    sleep(2)
                    # 复制到PC端文件夹内
                    copy_last_pic_to_local(WEI_XIN_FILE_PATH, adjunct_path)

                    # 如果是最后一张图片
                    if index == image_size - 1:
                        keyevent("BACK")
                    else:
                        # 向左滑动
                        poco.swipe([0.8, 0.5], [0.2, 0.5], duration=0.5)
                        sleep(2)

            # 下载视频
            if video_element.exists():
                video_element.click()
                poco('com.tencent.mm:id/ae5').long_click()
                del_files(WEI_XIN_FILE_PATH)
                poco(text='保存视频').click()
                sleep(2)
                # 复制到PC端文件夹内
                copy_last_pic_to_local(WEI_XIN_FILE_PATH, adjunct_path)
                keyevent('BACK')

            moods.append(item)

    return True


# 6.1 获取首页可以见的动态
write_to_csv(True, None)

i = 0
while True:
    # 判断是否是24小时之内的内容
    if not get_dynamic_moods():
        break

    print('休眠5秒后继续滑动')
    sleep(5)

    print('第%d次滑动' % i)
    poco.swipe([0.5, 0.6], [0.5, 0.2], duration=0.5)
    i += 1

# 写入到csv文件中
write_to_csv(False, moods)

print('恭喜，爬取数据成功！')
