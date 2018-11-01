package com.dnk.xinfeng902.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.dnk.xinfeng902.R;
import com.dnk.xinfeng902.utils.DevStateValue;
import com.dnk.xinfeng902.utils.Tools;

import java.util.Timer;
import java.util.TimerTask;

public class SetActivity extends Activity{

    //系统定时 1S
    private Timer timer;

    TextView version;
    TextView version_device_host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);

        version =findViewById(R.id.about_versionname);
        version_device_host =findViewById(R.id.device_version);

        version.setText(Tools.getVersionName(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerStart();
    }

    // 界面失去焦点时调用
    @Override
    protected void onPause() {
        super.onPause();
        timerStop();
    }

    /**
     * 时间启动与逻辑处理
     */
    private void timerStart() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //刷新UI必须执行在主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            version_device_host.setText(DevStateValue.version_device);
                        }
                    });
                }
            }, 20, 1000);
        }
    }
    /**
     * 时间停止
     */
    private void timerStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
