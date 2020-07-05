package com.xingag.network;

import java.util.HashMap;
import java.util.Map;

public class Header
{
    public static Map<String, String> generateHeader()
    {
        Map<String, String> header = new HashMap<>();
        header.put("authority", "www.iesdouyin.com");
        header.put("referer", "https://www.iesdouyin.com/share/video/6837860764536065295/?region=CN&mid=6811099886067796740&u_code=0&titleType=title&utm_source=copy_link&utm_campaign=client_share&utm_medium=android&app=aweme");
        header.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");
        header.put("Connection", "keep-alive");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.put("x-requested-with", "XMLHttpRequest");
        header.put("sec-fetch-site", "same-origin");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-dest", "empty");
        header.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        return header;
    }
}
