#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: dou_yin.py 
@time: 5/14/19 09:16 
@description：抖音自动化运营
"""

from utils.baidu_utils import *
from utils.device_utils import *
from utils.douyin_utils import *
import time
from datetime import datetime
import shutil


# 应用包名和Activity
package_name = 'com.ss.android.ugc.aweme'
activity_name = 'com.ss.android.ugc.aweme.splash.SplashActivity'

# 一条视频识别的最长时间
RECOGNITE_TOTAL_TIME = 10

poco = AndroidUiautomationPoco(use_airtest_input=True, screenshot_each_action=False)


def save_video_met():
    """
    :return:
    """
    # 分享
    os.system("adb shell input tap 1000 1500")
    time.sleep(0.05)

    # 保存到本地
    os.system("adb shell input tap 350 1700")

    time.sleep(1)

    # 等待视频保存成功
    wait_for_download_finished(poco)


if __name__ == '__main__':

    access_token = get_access_token()

    # 处理的次数
    handle_count = 0

    print('打开抖音~')
    # 打开抖音
    start_my_app(package_name, activity_name)

    time.sleep(5)

    while True:
        time.sleep(3)
        if is_a_ad():
            print('这是一条广告，过滤~')
            play_next_video()
            time.sleep(3)

        # 开始识别的时间
        recognite_time_start = datetime.now()

        # 识别次数
        recognite_count = 1

        # 循环地去刷抖音
        while True:
            if is_a_ad():
                print('这是一条广告，过滤~')
                play_next_video()
                time.sleep(3)
            # 获取截图
            print('开始第%d次截图' % recognite_count)

            # 截取屏幕有用的区域，过滤视频作者的头像、BGM作者的头像
            screen_name = get_screen_shot_part_img('images/temp%d.jpg' % recognite_count)

            # 人脸识别
            recognite_result = analysis_face(parse_face_pic(screen_name, TYPE_IMAGE_LOCAL, access_token))

            recognite_count += 1

            # 第n次识别结束后的时间
            recognite_time_end = datetime.now()

            # 这是一个美女
            if recognite_result:
                save_video_met()
                handle_count += 1
                print('识别到一个美女，继续下一个视频~')
                break
            else:
                if (recognite_time_end - recognite_time_start).seconds < RECOGNITE_TOTAL_TIME:
                    print('继续识别~')
                    continue
                else:
                    print('超时！！！这是一条没有吸引力的视频！')
                    # 跳出里层循环
                    break

        # 删除临时文件
        shutil.rmtree('./images')
        os.mkdir('./images')

        # 播放下一条视频
        print('==' * 30)
        time.sleep(2)
        print('准备播放下一个视频~')
        play_next_video()
        time.sleep(2)
