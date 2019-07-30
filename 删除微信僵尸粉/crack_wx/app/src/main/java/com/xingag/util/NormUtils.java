package com.xingag.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.xingag.crack_wx.MyApplication;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class NormUtils
{
    private static final String WX_SP_UIN_PATH = MyApplication.WX_ROOT_PATH + "shared_prefs/auth_info_key_prefs.xml";

    /**
     * 获取手机的imei码
     *
     * @return
     */
    public static String getPhoneIMEI()
    {

        TelephonyManager tm = (TelephonyManager) MyApplication.instance.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String mPhoneIMEI = tm.getDeviceId();
        return mPhoneIMEI;
    }


    /**
     * 获取微信的uids
     * 微信的uid存储在SharedPreferences里面
     * 存储位置\data\data\com.tencent.mm\shared_prefs\auth_info_key_prefs.xml
     * 我们解析xml用的dom4j这个库里面的SAXReader
     */
    public static String getUid()
    {
//        List<String> uids = new ArrayList<>();
        String uid = "";
        File file = new File(WX_SP_UIN_PATH);
        try
        {
            FileInputStream in = new FileInputStream(file);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(in);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            for (Element element : elements)
            {
                if ("_auth_uin".equals(element.attributeValue("name")))
                {
                    uid = element.attributeValue("value");
//                    uids.add(uid);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e("xag", "获取微信uid失败，请检查auth_info_key_prefs文件权限");
        }
        return uid;
    }


    /**
     * 根据imei和uin生成的md5码，获取数据库的密码（去前七位的小写字母）
     *
     * @param imei
     * @param uin
     * @return
     */
    public static String getDbPassword(String imei, String uin)
    {
        if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(uin))
        {
            Log.d("xag", "初始化数据库密码失败：imei或uid为空");
            return "密码错误";
        }
        String md5 = MD5Utils.md5(imei + uin);
        assert md5 != null;
        return md5.substring(0, 7).toLowerCase();
    }
}
