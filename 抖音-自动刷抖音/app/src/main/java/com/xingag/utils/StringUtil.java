package com.xingag.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{

    /***
     * 正则表达式匹配两个指定字符串中间的内容
     * @param source
     * @param rgex
     * @return
     */
    public static List<String> getSubUtil(String source, String rgex)
    {
        List<String> list = new ArrayList<>();
        // 匹配的模式
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(source);
        int i = 1;
        while (m.find())
        {
            list.add(m.group(i++));
        }
        return list;
    }

    /***
     * 利用正则表达式过滤出真实的视频地址
     * @param data
     * @return
     */
    public static String findUrlByStr(String data)
    {
        Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find())
        {
            return matcher.group();
        }
        return "";
    }


}
