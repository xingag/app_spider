package com.xingag.dy_auto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.easypermission.EasyPermission;
import com.easypermission.GrantResult;
import com.easypermission.Permission;
import com.easypermission.PermissionRequestListener;
import com.xingag.bean.VideoItem;
import com.xingag.bean.VideoNewItem;
import com.xingag.network.Header;
import com.xingag.utils.FileUtils;
import com.xingag.utils.StringUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();
    }

    private void initPermission()
    {
        try
        {
            //读写权限
            EasyPermission.with(this)
                    .addPermissions(Permission.Group.STORAGE)
                    .request(new PermissionRequestListener()
                    {
                        @Override
                        public void onGrant(Map<String, GrantResult> result)
                        {
                            //权限申请返回
                        }

                        @Override
                        public void onCancel(String stopPermission)
                        {
                        }
                    });
        } catch (Exception e)
        {
            Log.d("xag", "申请权限产生异常");
        }
    }
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                try
                {
                    //获取重定向的url
                    url = Jsoup.connect(url)
                            .followRedirects(true)
                            .execute().url().toExternalForm();

                    //从重定向后的地址，解析出视频id，即：item_ids
                    //https://www.iesdouyin.com/share/video/6837860764536065295/?region=CN&mid=6811099886067796740&u_code=0&titleType=title&utm_source=copy_link&utm_campaign=client_share&utm_medium=android&app=aweme
                    Log.e("xag", "重定向地址：\n" + url);
                    String item_id = StringUtil.getSubUtil(url, "video/(.*?)/\\?region").get(0);
                    Log.d("xag", "item_id:" + item_id);

                    //通过新链接地址，重新获取内容（网站改版了，反爬）
                    //String resp = Jsoup.connect(url).get().toString();
                    //String resp = Jsoup.connect(url).ignoreContentType(true).headers(Header.generateHeader()).get().body().html();

//                    Log.d("xag","写入路径:"+logPath);
//                    FileUtils.writeFile(logPath, resp,true);
//                    Log.d("xag","resp:"+resp);

                    //获取参数：dytk
//                    String dytk = StringUtil.getSubUtil(resp, "dytk: \"(.*?)\"").get(0);
//                    Log.d("xag", "dytk:" + dytk);

                    //获取数据
                    //https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=6843210772533890317
//                    String new_url = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + item_id + "&dytk=" + dytk;
                    String new_url = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + item_id;
                    Connection connection = Jsoup.connect(new_url).ignoreContentType(true);
                    Connection data = connection.headers(Header.generateHeader());
                    String result = data.get().body().html();

                    Log.d("xag","result:"+result);

                    VideoNewItem item = JSON.parseObject(result,VideoNewItem.class);
                    //毫秒
                    int timeout = item.getItem_list().get(0).getDuration();
                    Log.d("xag","视频时长0:"+timeout);
                } catch (Exception e)
                {
                    Log.d("xag", "产生异常,异常信息:" + e.getMessage());
                }
            }
        }.start();
    }
}

