package com.xingag.readmsg;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.anim.DefaultAnimator;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.xingag.bean.ShowFloatBean;
import com.xingag.utils.SpUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

//欢迎关注公众号：AirPython，获取Python、自动化技术干货

public class MainActivity extends AppCompatActivity
{

    private Button speak_tv, stop_tv = null;


    private String content = "欢迎关注公众号：AirPython，获取Python、自动化技术干货";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        speak_tv = findViewById(R.id.speak_tv);
        stop_tv = findViewById(R.id.stop_tv);
        speak_tv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                BaseApplication.getInstance().speakContent("欢迎关注公众号：AirPython，获取Python、自动化技术干货");
                BaseApplication.getInstance().speakContent("欢迎关注公众号：AirPython，获取Python、自动化技术干货");
                BaseApplication.getInstance().speakContent("欢迎关注公众号：AirPython，获取Python、自动化技术干货");
            }
        });

        stop_tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BaseApplication.getInstance().stopSpeak();
            }
        });

        //权限检查
        PermissionUtils.checkPermission(this);

        initFloatDialog();
    }

    private void initFloatDialog()
    {
        View currentFLoat = EasyFloat.getAppFloatView("readmsg");
        if (null == currentFLoat)
        {
            //付款
            EasyFloat.with(this).setLayout(R.layout.float_test, new OnInvokeView()
            {
                @Override
                public void invoke(View view)
                {
                    ImageView close_iv = view.findViewById(R.id.ivClose);
                    final CheckBox float_cb = view.findViewById(R.id.float_cb);
                    close_iv.setOnClickListener(new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {
                            Log.d("xag", "点击事件");
                            //关闭系统浮框
                            EasyFloat.dismissAppFloat("readmsg");
                        }
                    });
                    float_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {
                            Log.d("xag", "currentClassName:" + MsgService.currentClassName);
                            boolean isArticlPage = TextUtils.equals(MsgService.currentClassName, MsgService.CLASS_NAME_PAGE_ARTICLE);
                            //如果不在文章界面，则不作处理
                            if (!isArticlPage)
                            {
                                Toast.makeText(BaseApplication.getInstance(), "请在微信文章页面操作", Toast.LENGTH_SHORT).show();
                                float_cb.setChecked(false);
                                isChecked = false;
                            }
                            float_cb.setText(isChecked ? "停止" : "播放");
                            if (isChecked)
                            {
                                String content = SpUtil.get("contents", "");
                                Log.d("xag", "要播放的内容如下：");
                                Log.d("xag", content);
                                String[] contents = content.split("；；；");
                                //注意太长没法直接播放
                                for (String item : contents)
                                {
                                    BaseApplication.getInstance().speakContent(item);
                                }
                            } else
                            {
                                BaseApplication.getInstance().stopSpeak();
                            }
                        }
                    });

                }
            }).setShowPattern(ShowPattern.ALL_TIME)
                    .setTag("readmsg")
                    .setAnimator(new DefaultAnimator())
                    .setGravity(Gravity.END | Gravity.CENTER_VERTICAL, -2, 200).show();
        }

        if (!EasyFloat.isShow(this, "readmsg"))
        {
            EasyFloat.showAppFloat("readmsg");
        }
    }

    @Subscriber
    private void changeFloatStatus(ShowFloatBean showFloatBean)
    {
        Log.d("xag", "接受到事件,展示或者隐藏：" + showFloatBean.isShow());
        boolean showFloat = showFloatBean.isShow();
        if (showFloat)
        {
            initFloatDialog();
        } else
        {
            EasyFloat.dismissAppFloat("readmsg");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("xag", "onDestory");
        EasyFloat.hideAppFloat("readmsg");
    }

}
