#!/usr/bin/env python  
# encoding: utf-8  

""" 
@version: v1.0 
@author: xag 
@license: Apache Licence  
@contact: xinganguo@gmail.com 
@site: http://www.xingag.top 
@software: PyCharm 
@file: cmd_utils.py 
@time: 2019-09-06 15:28 
@description：TODO
"""

import tempfile
import subprocess


# 启用子进程执行外部shell命令
def exec_cmd(cmd):
    global out_temp, rt_list
    try:
        # 得到一个临时文件对象， 调用close后，此文件从磁盘删除
        out_temp = tempfile.TemporaryFile(mode='w+')
        # 获取临时文件的文件号
        fileno = out_temp.fileno()

        # 执行外部shell命令， 输出结果存入临时文件中
        p = subprocess.Popen(cmd, shell=True, stdout=fileno, stderr=fileno)
        p.wait()

        # 从临时文件读出shell命令的输出结果
        out_temp.seek(0)
        rt = out_temp.read()

        # 以换行符拆分数据，并去掉换行符号存入列表
        rt_list = rt.strip().split('\n')

    except Exception as e:
        print(e.format_exc())

    finally:
        if out_temp:
            out_temp.close()

    return rt_list


