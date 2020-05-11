package com.xingag.readmsg;

import android.app.Application;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.lzf.easyfloat.EasyFloat;


import java.util.HashMap;
import java.util.Locale;


public class BaseApplication extends Application
{

    public static BaseApplication instance;

    //定义一个tts对象
    private TextToSpeech tts;
    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener()
    {

        @Override
        public void onInit(int status)
        {
            // 判断是否转化成功
            if (status == TextToSpeech.SUCCESS)
            {
                //默认设定语言为中文，原生的android貌似不支持中文。
                //很多设备不支持中文
                int result = tts.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                {
                    Toast.makeText(getApplicationContext(), "不支持中文,请先安装【文字转语音服务】", Toast.LENGTH_SHORT).show();
                } else
                {
                    //不支持中文就将语言设置为英文
                    tts.setLanguage(Locale.US);
                }
            }
        }
    };


    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;

        initTTS();

        //悬浮窗
        EasyFloat.init(this, true);
    }

    private void initTTS()
    {
        //初始化tts监听对象
        tts = new TextToSpeech(this, onInitListener);

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){

            @Override
            public void onStart(String utteranceId)
            {

            }

            @Override
            public void onDone(String utteranceId)
            {

            }

            @Override
            public void onError(String utteranceId)
            {

            }
        });
        //音调设置
        tts.setPitch(1.0f);
        //语音音速
        tts.setSpeechRate(0.8f);
    }

    public static BaseApplication getInstance()
    {
        return instance;
    }


    /***
     * 播放方法的封装
     */
    public void speakContent(String content)
    {
        if (null == tts)
        {
            initTTS();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            tts.speak(content, TextToSpeech.QUEUE_ADD, null, null);
        } else
        {
            tts.speak(content, TextToSpeech.QUEUE_ADD, null);
        }
    }

    /***
     * 停止播放
     */
    public void stopSpeak()
    {
        if (null != tts && tts.isSpeaking())
        {
            tts.stop();
        }
    }

    /***
     * 释放资源
     */
    public void releaseTTS()
    {
        if (null != tts)
        {
            //不管是不是在阅读,都打断
            tts.stop();
            //关闭,释放资源
            tts.shutdown();
            tts = null;
        }

    }

}
