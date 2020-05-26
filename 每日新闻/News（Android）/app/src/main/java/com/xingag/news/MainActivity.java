package com.xingag.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button get_news_btn = null;

    private TextView news_et = null;

    //API服务地址
    private String url = "http://**:8000/last_news";

    private Handler uiHandler;

    {
        uiHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case 1:
                        String news = (String) msg.obj;
                        copyToClip(news);
                        news_et.setText(news);
                        break;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("每日新闻");

        news_et = findViewById(R.id.news_et);
        get_news_btn = findViewById(R.id.get_news_btn);

        //监听事件
        get_news_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.get_news_btn:
                news_et.setText("获取中。。。");
                getNewsMet();
                break;
        }
    }

    /***
     * 获取新闻
     */
    private void getNewsMet()
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        //构建请求信息：连接请求url 请求方法method 请求头部headers 请求体body 标签tag
        Request request = new Request.Builder().url(url).get().build();
//        Call call = okHttpClient.newCall(request);

        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.d("xag", "获取失败");
                showResult(false, "");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException
            {
                Log.d("xag", "获取成功");

                parseJsonWithJsonObject(response);

            }
        });

    }

    /***
     * 解析数据
     * @param
     */
    private void parseJsonWithJsonObject(final Response response)
    {
        try
        {
            String responseData = response.body().string();
            JSONObject jsonArray = new JSONObject(responseData);
            int code = jsonArray.getInt("code");
            String news = jsonArray.getString("news");

            Log.d("xag", "news:" + news);
            showResult(true, news);

//            news_et.setText(news);
//            Message msg = new Message();
//            msg.obj = news;
//            msg.what = 1;
//            uiHandler.sendMessage(msg);

        } catch (JSONException | IOException e)
        {
//            showToast("解析异常");
            Log.d("xag", "解析异常");
            e.printStackTrace();
        }
    }


    private void copyToClip(String content)
    {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        if (null != cm)
        {
            cm.setPrimaryClip(mClipData);
        }
    }

    /***
     * 显示结果
     * @param success
     * @param content
     */
    private void showResult(final boolean success, final String content)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                copyToClip(content);
                news_et.setText(content);
                final String msg = success ? "获取成功" : "获取失败";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
    }
}
