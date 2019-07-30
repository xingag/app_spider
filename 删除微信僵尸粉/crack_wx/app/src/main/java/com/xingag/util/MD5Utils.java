package com.xingag.util;

import java.security.MessageDigest;

public class MD5Utils
{

    /**
     * md5加密
     *
     * @param content
     * @return
     */
    public static String md5(String content)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();//加密
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++)
            {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1)
                {
                    sb.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else
                {
                    sb.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return sb.toString();
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
