package com.xingag.xianyu;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suke.widget.SwitchButton;
import com.xingag.base.BaseService;
import com.xingag.util.Injector;
import com.xingag.util.SnackbarUtils;
import com.xingag.xianyu.service.XianYuService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //状态
    private TextView status_tv = null;

    //管理服务,常规操作
    private Button start_service_btn = null;

    private TextView norm_operate_btn = null;

    private SwitchButton switchButton = null;

    //设置发货内容
    private Button delivery_rebot_set_content_btn = null;

    //发货内容输入框
    private EditText delivery_rebot_content_et = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status_tv = findViewById(R.id.status_tv);
        start_service_btn = findViewById(R.id.start_service_btn);
        norm_operate_btn = findViewById(R.id.norm_operate_btn);

        delivery_rebot_content_et = findViewById(R.id.delivery_rebot_content_et);

        switchButton = findViewById(R.id.delivery_rebot_switch);
        delivery_rebot_set_content_btn = findViewById(R.id.delivery_rebot_set_content_btn);


        start_service_btn.setOnClickListener(this);
        norm_operate_btn.setOnClickListener(this);
        delivery_rebot_set_content_btn.setOnClickListener(this);

        //自动发货机器人的状态
        Boolean status = SettingConfig.getInstance().getAutoDeliverStatus();
        changeStatusMet(status);

        //设置默认状态
        switchButton.setChecked(SettingConfig.getInstance().getAutoDeliverStatus());

        //自动发货是否打开
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked)
            {
                String tips = isChecked ? "自动发货机器人开启" : "自动发货机器人关闭";
                SnackbarUtils.Long(delivery_rebot_set_content_btn, tips).show();
                SettingConfig.getInstance().setAutoDeliverStatus(isChecked);
                changeStatusMet(isChecked);
            }
        });

        //需要打开一次闲鱼，防止闲鱼应用还未开启
//        startApp(Constants.packageName_xianyu);

        //回退到当前应用
//        startApp(Constants.packageName_local);

        initStatus();

        Intent mIntent = new Intent(this, XianYuService.class);
        startService(mIntent);
    }

    /***
     * 更改状态
     * @param status
     */
    private void changeStatusMet(boolean status)
    {
        String status_content = status ? "开启" : "关闭";
        norm_operate_btn.setText(String.format(getResources().getString(R.string.delivery_rebot_operation), status_content));
    }


    /***
     * 打开某个应用
     * @param packageName
     */
    private void startApp(String packageName)
    {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (null == intent)
        {
            Toast.makeText(this, "目标未安装", Toast.LENGTH_LONG).show();
        } else
        {
            startActivity(intent);
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        initStatus();
    }

    /***
     * 初始化服务状态
     */
    private void initStatus()
    {
        boolean status = BaseService.getInstance().checkAccessibilityEnabled(Constants.serviceName);
        changeStatus(status);
    }

    /***
     * 服务显示状态
     * @param status
     */
    private void changeStatus(boolean status)
    {
        if (status)
        {
            start_service_btn.setVisibility(View.GONE);
            status_tv.setText(getResources().getString(R.string.status, getString(R.string.on)));
        } else
        {
            start_service_btn.setVisibility(View.VISIBLE);
            status_tv.setText(getResources().getString(R.string.status, getString(R.string.off)));
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.start_service_btn:
                //跳转到【无障碍服务】界面
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.delivery_rebot_set_content_btn:
                //输入的发货地址，比如网盘地址
                String content = delivery_rebot_content_et.getEditableText().toString().trim();
                if (TextUtils.isEmpty(content))
                {
                    SnackbarUtils.Short(delivery_rebot_set_content_btn, "请先输入要发货的内容").show();
                } else
                {
                    SettingConfig.getInstance().setAutoDeliverContent(content);
                    delivery_rebot_content_et.getEditableText().clear();
                    SnackbarUtils.Long(delivery_rebot_set_content_btn, "设置发货成功！！！").show();
                }
                break;
            default:
                break;
        }
    }
}
