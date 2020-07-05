package com.xingag.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileUtils
{
    /***
     * 读取文件
     * @param filePath
     * @param charsetName
     * @return
     */
    public static String readFile(String filePath, String charsetName)
    {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (!file.isFile())
        {
            return "";
        }

        BufferedReader reader = null;
        try
        {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if (!fileContent.toString().equals(""))
                {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent.toString();
        } catch (IOException e)
        {
            throw new RuntimeException("IOException occurred. ", e);
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }


    /***
     * 写入到文件中
     * @param filePath
     * @param content
     * @param append
     * @return
     */
    public static boolean writeFile(String filePath, String content, boolean append)
    {
        if (TextUtils.isEmpty(content))
        {
            return false;
        }

        FileWriter fileWriter = null;
        try
        {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content + "\n");
            return true;
        } catch (IOException e)
        {
            throw new RuntimeException("IOException occurred. ", e);
//            return false;
        } finally
        {
            if (fileWriter != null)
            {
                try
                {
                    fileWriter.close();
                } catch (IOException e)
                {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static String getFolderName(String filePath)
    {
        if (TextUtils.isEmpty(filePath))
        {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static boolean makeDirs(String filePath)
    {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName))
        {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }


    public static String StrMD5(String data)
    {
        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(data.getBytes(), 0, data.getBytes().length);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static String FileMD5(File file)
    {
        if (!file.isFile())
        {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1)
            {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /***
     * 获取文件总大小(字节)
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath)
    {
        File file = new File(filePath);
        long length = -1;
        if (file.exists())
        {
            length = file.length();
        }
        return length;
    }

}
