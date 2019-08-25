package com.xingag.xianyu;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xingag.base.BaseService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //状态
    private TextView status_tv = null;

    //管理服务
    private Button start_service_btn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status_tv = findViewById(R.id.status_tv);
        start_service_btn = findViewById(R.id.start_service_btn);

        start_service_btn.setOnClickListener(this);

        initStatus();
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
            default:
                break;
        }
    }
}
