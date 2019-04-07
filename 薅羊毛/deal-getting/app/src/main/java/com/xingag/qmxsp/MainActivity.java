package com.xingag.qmxsp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xingag.utils.CMDUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button start_qmxsp_tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewMet();
        initListenerMet();
    }


    private void initViewMet()
    {
        start_qmxsp_tv = findViewById(R.id.start_qmxsp_tv);
    }

    private void initListenerMet()
    {
        start_qmxsp_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.start_qmxsp_tv:
                Log.d("xag","点击");
                openQmxspMet();
                break;

            default:
                break;
        }
    }

    /**
     * 生成命令
     *
     * @param pkgName uiautomator包名
     * @param clsName uiautomator类名
     * @param mtdName uiautomator方法名
     * @return
     */
    public String generateCommand(String pkgName, String clsName, String mtdName)
    {
        String command = "am instrument -w -r -e debug false -e class "
                + pkgName + "." + clsName + "#" + mtdName + " "
                + pkgName + ".test/android.support.test.runner.AndroidJUnitRunner";
        return command;
    }

    /***
     * 打开全名小视频应用
     * 注意：必须配置签名，才能正常启动应用
     */
    private void openQmxspMet()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                String command = generateCommand("com.xingag.qmxsp", "GetDeal", "start_qmxsp");
                CMDUtils.CMD_Result rs = CMDUtils.runCMD(command, true, true);
            }
        }.start();

    }
}
