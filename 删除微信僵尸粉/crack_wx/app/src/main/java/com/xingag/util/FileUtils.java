package com.xingag.util;

import com.xingag.bean.Contact;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils
{
    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath)
    {
        try
        {
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists())
            { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1)
                {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e)
        {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    /***
     * 写入数据到csv中
     * @param output_path
     * @param contacts
     */
    public static void writeCsvFile(String output_path, List<Contact> contacts)
    {
        try
        {
            File file = new File(output_path);
            //删除之前保存的文件
            if (file.exists())
            {
                file.delete();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            // 添加头部名称
            bw.write("userName" + "," + "alias" + "," + "nickName");
            bw.newLine();
            for (int i = 0; i < contacts.size(); i++)
            {
                bw.write(contacts.get(i).getUserName() + "," + contacts.get(i).getAlias() + "," + contacts.get(i).getNickName());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
