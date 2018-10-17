package com.dnk.xinfeng902.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnk.xinfeng902.R;
import com.dnk.xinfeng902.rs455.RS485Function;
import com.dnk.xinfeng902.utils.Config;
import com.dnk.xinfeng902.utils.DataStorage;
import com.dnk.xinfeng902.utils.DevStateValue;
import com.dnk.xinfeng902.utils.Tools;
import com.dnk.xinfeng902.utils.dataBase.DataBaseUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    //数据存储区
    public DataStorage dataStorage;

    //系统定时器定时器
    public static int sysTimerCount = 10;
    public static int databaseSaveTimerCount = 0;//10分钟保存一次数据库

    //发送给主板的轮询命令类型：控制参数与查询温度轮流
    public static boolean hostSendType = true;

    //系统定时 1S
    public Timer functionSysTimer;

    //485通信相关
    private RS485Function rS485Function;

    //智能模式和强劲模式时频繁变化的，为了省资源，只有变化时才更新UI
    private static int modeRun_runloop_last=0xff;
    private static int modeRun_fanspeed_last=0xff;
    private static int modeRun_coolmode_last=0xff;
    private static int modeRun_coolspeed_last=0xff;

    TextView sensor_air_value;
    TextView sensor_pm25_value;
    TextView sensor_co2_value;
    TextView sensor_tvoc_value;
    TextView sensor_temp_value;
    TextView sensor_humidity_value;
    TextView set_temp;

    ImageView sensor_pm25_image;
    ImageView sensor_co2_image;
    ImageView sensor_tvoc_image;
    ImageView sensor_temp_image;
    ImageView sensor_humidity_image;

    ImageButton power_open;
    ImageButton power_close;
    ImageButton mode_hand;
    ImageButton mode_smart;
    ImageButton mode_powerful;
    ImageButton circulation_in;
    ImageButton fanspeed_heigth;
    ImageButton circulation_out;
    ImageButton auheat;
    ImageButton fanspeed_low;
    ImageButton fanspeed_middle;
    ImageButton cool_mode_refrigera;
    ImageButton cool_mode_heat;
    ImageButton cool_mode_ventilation;
    ImageButton cool_speed_low;
    ImageButton cool_speed_middle;
    ImageButton cool_speed_heigth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dataStorage =new DataStorage();
        dataStorage.SharedPreferences2Data(this);

        sensor_air_value =findViewById(R.id.air_value);

        sensor_pm25_value =findViewById(R.id.pm25_value);
        sensor_co2_value =findViewById(R.id.co2_value);
        sensor_tvoc_value =findViewById(R.id.tvoc_value);
        sensor_temp_value =findViewById(R.id.temp_value);
        sensor_humidity_value =findViewById(R.id.humidity_value);

        sensor_pm25_image =findViewById(R.id.pm25_image);
        sensor_co2_image =findViewById(R.id.co2_image);
        sensor_tvoc_image =findViewById(R.id.tvoc_image);
        sensor_temp_image =findViewById(R.id.temp_image);
        sensor_humidity_image =findViewById(R.id.humidity_image);

        power_open = findViewById(R.id.power_open);
        power_close = findViewById(R.id.power_close);
        mode_hand = findViewById(R.id.mode_hand);
        mode_smart = findViewById(R.id.mode_smart);
        mode_powerful = findViewById(R.id.mode_powerful);

        set_temp = findViewById(R.id.temp_set_value);

        circulation_in = findViewById(R.id.circulation_in);
        circulation_out = findViewById(R.id.circulation_out);
        auheat = findViewById(R.id.auheat);
        fanspeed_low = findViewById(R.id.fanspeed_low);
        fanspeed_middle = findViewById(R.id.fanspeed_middle);
        fanspeed_heigth = findViewById(R.id.fanspeed_heigth);

        cool_mode_refrigera = findViewById(R.id.cool_mode_refrigera);
        cool_mode_heat = findViewById(R.id.cool_mode_heat);
        cool_mode_ventilation = findViewById(R.id.cool_mode_ventilation);
        cool_speed_low = findViewById(R.id.cool_speed_low);
        cool_speed_middle = findViewById(R.id.cool_speed_middle);
        cool_speed_heigth = findViewById(R.id.cool_speed_heigth);

        //485函数创建
        rS485Function =new RS485Function();

        //接收到查询到数据时更新UI
        RS485Function.receiverDataUpdateUIListenerInit(new RS485Function.receiverDataUpdateUIListener() {
            @Override
            public void receiver_updateUI(byte[] replyData) {
                mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_SENSOR_UI);
            }
        });
    }
    // 应用程序界面获得焦点时调用
    @Override
    protected void onResume() {
        super.onResume();
        sysTimerStart();
        databaseSaveTimerCount = 590;//到主界面10s后保存一次
        mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_CONTROL_UI);
    }

    // 界面失去焦点时调用
    @Override
    protected void onPause() {
        super.onPause();
        sysTimerStop();
    }

    // 当界面被销毁时调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置按钮点击
     */
    public void onOterViewClk(View view) {
        switch (view.getId()) {
            case R.id.list:
                Intent intent1 = new Intent(this,ListActivity.class);
                startActivity(intent1);
                break;
            case R.id.set:
                rS485Function.send486DataControl(Config.HANDEL_SEND_GET_VERSION);
                Intent intent2 = new Intent(this,SetActivity.class);
                startActivity(intent2);
                break;
        }
    }
    /**
     *  模式/温度设置按钮点击
     */
    public void onControl_Mode_TempSet_Clk(View view){

        //设备关闭时，只有开按键可以点击
        if(!DevStateValue.powerswitch && (view.getId()!=R.id.power_open)){
            Tools.showToast(this,"请先开启设备");
            return;
        }

        switch (view.getId()) {
            case R.id.power_open:
                DevStateValue.powerswitch = true;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_POWER);
                break;
            case R.id.power_close:
                DevStateValue.powerswitch = false;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_POWER);
                break;
            case R.id.mode_hand:
                DevStateValue.mode = Config.MODE_HAND;
                break;
            case R.id.mode_smart:
                DevStateValue.mode = Config.MODE_SMART;
                break;
            case R.id.mode_powerful:
                DevStateValue.mode = Config.MODE_POWERFUL;
                break;
            case R.id.temp_set_add:
                if(DevStateValue.mode == Config.MODE_HAND && DevStateValue.set_temp_hand <32) DevStateValue.set_temp_hand ++;
                if(DevStateValue.mode == Config.MODE_SMART && DevStateValue.set_temp_smart <32) DevStateValue.set_temp_smart ++;
                if(DevStateValue.mode == Config.MODE_POWERFUL && DevStateValue.set_temp_powerful <32) DevStateValue.set_temp_powerful ++;
                break;
            case R.id.temp_set_minus:
                if(DevStateValue.mode == Config.MODE_HAND && DevStateValue.set_temp_hand >16) DevStateValue.set_temp_hand --;
                if(DevStateValue.mode == Config.MODE_SMART && DevStateValue.set_temp_smart >16) DevStateValue.set_temp_smart --;
                if(DevStateValue.mode == Config.MODE_POWERFUL && DevStateValue.set_temp_powerful >16) DevStateValue.set_temp_powerful --;
                break;
        }
        mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_CONTROL_UI);
    }
    /**
     *  新风按钮点击
     */
    public void onControl_Air_Clk(View view){

        //设备关闭时，只有开按键可以点击
        if(!DevStateValue.powerswitch && (view.getId()!=R.id.power_open)){
            Tools.showToast(this,"请先开启设备");
            return;
        }
        //智能/强劲模式时，不可点击
        if((DevStateValue.mode ==Config.MODE_SMART)||(DevStateValue.mode ==Config.MODE_POWERFUL)){
            Tools.showToast(this,"手动模式才有效");
            return;
        }

        switch (view.getId()) {
            case R.id.circulation_in:
                DevStateValue.runloop = 1;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_RUNLOOP);
                break;
            case R.id.circulation_out:
                DevStateValue.runloop = 2;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_RUNLOOP);
                break;
            case R.id.auheat:
                DevStateValue.heat = DevStateValue.heat?false:true;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_HEAT);
                break;
            case R.id.fanspeed_low:
                DevStateValue.fanspeed =1;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_FANSPEED);
                break;
            case R.id.fanspeed_middle:
                DevStateValue.fanspeed =4;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_FANSPEED);
                break;
            case R.id.fanspeed_heigth:
                DevStateValue.fanspeed =7;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_FANSPEED);
        }
        mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_CONTROL_UI);
    }

    /**
     *  空调按钮点击
     */
    public void onControl_Cool_Clk(View view){

        //设备关闭时，只有开按键可以点击
        if(!DevStateValue.powerswitch && (view.getId()!=R.id.power_open)){
            Tools.showToast(this,"请先开启设备");
            return;
        }

        //智能/强劲模式时，不可点击
        if((DevStateValue.mode ==Config.MODE_SMART)||(DevStateValue.mode ==Config.MODE_POWERFUL)){
            Tools.showToast(this,"手动模式才有效");
            return;
        }

        switch (view.getId()) {

            case R.id.cool_mode_refrigera:
                DevStateValue.cool_mode =1;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_COOLMODE);
                break;
            case R.id.cool_mode_heat:
                DevStateValue.cool_mode =2;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_COOLMODE);
                break;
            case R.id.cool_mode_ventilation:
                DevStateValue.cool_mode =0;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_COOLMODE);
                break;
            case R.id.cool_speed_low:
                DevStateValue.cool_speed =1;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_COOLSPEED);
                break;
            case R.id.cool_speed_middle:
                DevStateValue.cool_speed =4;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_COOLSPEED);
                break;
            case R.id.cool_speed_heigth:
                DevStateValue.cool_speed =7;
                mHandler.sendEmptyMessage(Config.HANDEL_SEND_CONTROL_COOLSPEED);
                break;

        }
        mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_CONTROL_UI);
    }

    /**
     * 逻辑事件队列处理
     */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Config.HANDEL_SEND_SENSOR:
                case Config.HANDEL_SEND_HOST_CONTROL:
                case Config.HANDEL_SEND_HOST_GETSTATE:
                    rS485Function.send486DataControl(msg.what);
                    break;
                case Config.HANDEL_SEND_CONTROL_POWER:
                case Config.HANDEL_SEND_CONTROL_RUNLOOP:
                case Config.HANDEL_SEND_CONTROL_HEAT:
                case Config.HANDEL_SEND_CONTROL_FANSPEED:
                case Config.HANDEL_SEND_CONTROL_COOLMODE:
                case Config.HANDEL_SEND_CONTROL_COOLSPEED:
                    sysTimerCount=0;
                    rS485Function.send486DataControl(msg.what);
                    break;
                case Config.HANDEL_UPDATA_CONTROL_UI:
                    updateControlUI();
                    break;
                case Config.HANDEL_UPDATA_SENSOR_UI:
                    updataSensorUI();
                    break;
                case Config.HANDEL_UPDATA_MODERUN_UI:
                    updataModeRunFunctionUI();
                    break;
                case Config.HANDEL_MODE_RUN_RFUNCTION:
                    modeRunFunction();
                    break;
                case Config.HANDEL_SAVE_DATABASE:
                    DataBaseUtil.add();
                    break;
            }
        }
    };

    /**
     *  系统模式运行逻辑
     *  1S调用一次
     */
    public void modeRunFunction(){

        if(!DevStateValue.powerswitch) return;

        switch (DevStateValue.mode){

            case  Config.MODE_HAND:
                break;

            case Config.MODE_SMART:
                DevStateValue.runloop =1;
                DevStateValue.fanspeed =1;
                DevStateValue.cool_speed =4;
                //逻辑：
                //采集不到实际温度时，则切换为通风
                //实际温度比设置温度高2度以上，则切换为制冷。
                //设置温度比实际温度高2度以上，则切换为制热。
                if(DevStateValue.hostCooltemp ==Config.NOTHING) DevStateValue.cool_mode = Config.COOL_MODE_VENTILATION;
                else if((DevStateValue.hostCooltemp-DevStateValue.set_temp_powerful)>=2) DevStateValue.cool_mode = Config.COOL_MODE_REFRIGERA;
                else if((DevStateValue.set_temp_powerful -DevStateValue.hostCooltemp)>=2) DevStateValue.cool_mode = Config.COOL_MODE_HOST;

                mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_MODERUN_UI);
                break;

            case Config.MODE_POWERFUL:
                DevStateValue.runloop =1;

                if(DevStateValue.airGrade ==Config.AIR_GRADE_BAD)
                    DevStateValue.fanspeed =7;
                if(DevStateValue.airGrade ==Config.AIR_GRADE_LIANG)
                    DevStateValue.fanspeed =4;
                else
                    DevStateValue.fanspeed =1;

                DevStateValue.cool_speed =7;
                //逻辑：
                //采集不到实际温度时，则切换为通风
                //实际温度比设置温度高2度以上，则切换为制冷。
                //设置温度比实际温度高2度以上，则切换为制热。
                if(DevStateValue.hostCooltemp ==Config.NOTHING) DevStateValue.cool_mode = Config.COOL_MODE_VENTILATION;
                else if((DevStateValue.hostCooltemp-DevStateValue.set_temp_powerful)>=2) DevStateValue.cool_mode = Config.COOL_MODE_REFRIGERA;
                else if((DevStateValue.set_temp_powerful -DevStateValue.hostCooltemp)>=2) DevStateValue.cool_mode = Config.COOL_MODE_HOST;

                mHandler.sendEmptyMessage(Config.HANDEL_UPDATA_MODERUN_UI);
                break;
        }
    }

    /**
     * 更新界面的控制性UI
     */
    private void updateControlUI() {

        if(DevStateValue.powerswitch){

            power_open.setImageResource(R.mipmap.kai_up);
            power_close.setImageResource(R.mipmap.guan_dn);

            if(DevStateValue.mode==Config.MODE_HAND){
                mode_hand.setImageResource(R.mipmap.mode_hand_up);
                mode_smart.setImageResource(R.mipmap.mode_smart_dn);
                mode_powerful.setImageResource(R.mipmap.mode_powerful_dn);
            }else if(DevStateValue.mode==Config.MODE_SMART){
                mode_hand.setImageResource(R.mipmap.mode_hand_dn);
                mode_smart.setImageResource(R.mipmap.mode_smart_up);
                mode_powerful.setImageResource(R.mipmap.mode_powerful_dn);
            }else{
                mode_hand.setImageResource(R.mipmap.mode_hand_dn);
                mode_smart.setImageResource(R.mipmap.mode_smart_dn);
                mode_powerful.setImageResource(R.mipmap.mode_powerful_up);
            }

            if(DevStateValue.runloop ==1){
                circulation_in.setImageResource(R.mipmap.circulation_in_up);
                circulation_out.setImageResource(R.mipmap.circulation_out_dn);
            }else{
                circulation_in.setImageResource(R.mipmap.circulation_in_dn);
                circulation_out.setImageResource(R.mipmap.circulation_out_up);
            }

            if(DevStateValue.heat)  auheat.setImageResource(R.mipmap.auheat_up);
            else  auheat.setImageResource(R.mipmap.auheat_dn);

            if(DevStateValue.fanspeed==1){
                fanspeed_low.setImageResource(R.mipmap.fanspeed_low_up);
                fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_dn);
                fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_dn);
            }else if(DevStateValue.fanspeed==4){
                fanspeed_low.setImageResource(R.mipmap.fanspeed_low_dn);
                fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_up);
                fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_dn);
            }else{
                fanspeed_low.setImageResource(R.mipmap.fanspeed_low_dn);
                fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_dn);
                fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_up);
            }

            if(DevStateValue.cool_mode==1){
                cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_up);
                cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_dn);
                cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_dn);
            }else if(DevStateValue.cool_mode==2){
                cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_dn);
                cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_up);
                cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_dn);
            }else{
                cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_dn);
                cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_dn);
                cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_up);
            }

            if(DevStateValue.cool_speed==1){
                cool_speed_low.setImageResource(R.mipmap.cool_speed_low_up);
                cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_dn);
                cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_dn);
            }else if(DevStateValue.cool_speed==4){
                cool_speed_low.setImageResource(R.mipmap.cool_speed_low_dn);
                cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_up);
                cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_dn);
            }else{
                cool_speed_low.setImageResource(R.mipmap.cool_speed_low_dn);
                cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_dn);
                cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_up);
            }

            if(DevStateValue.mode ==Config.MODE_HAND)  set_temp.setText(""+DevStateValue.set_temp_hand);
            else if(DevStateValue.mode ==Config.MODE_SMART) set_temp.setText(""+DevStateValue.set_temp_smart);
            else set_temp.setText(""+DevStateValue.set_temp_powerful);

        } else {
            power_open.setImageResource(R.mipmap.kai_dn);
            power_close.setImageResource(R.mipmap.guan_up);
            //power_close.setEnabled(false);

            mode_hand.setImageResource(R.mipmap.mode_hand_dn);
            mode_smart.setImageResource(R.mipmap.mode_smart_dn);
            mode_powerful.setImageResource(R.mipmap.mode_powerful_dn);

            circulation_in.setImageResource(R.mipmap.circulation_in_dn);
            circulation_out.setImageResource(R.mipmap.circulation_out_dn);
            auheat.setImageResource(R.mipmap.auheat_dn);
            fanspeed_low.setImageResource(R.mipmap.fanspeed_low_dn);
            fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_dn);
            fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_dn);

            cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_dn);
            cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_dn);
            cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_dn);
            cool_speed_low.setImageResource(R.mipmap.cool_speed_low_dn);
            cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_dn);
            cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_dn);

            set_temp.setText("—");
        }

        dataStorage.Data2SharedPreferences(this);
    }
    /**
     * 在模式逻辑时UI相关当UI更新
     */
    private void updataModeRunFunctionUI(){

        if(modeRun_runloop_last != DevStateValue.runloop) {
            if (DevStateValue.runloop == 1) {
                circulation_in.setImageResource(R.mipmap.circulation_in_up);
                circulation_out.setImageResource(R.mipmap.circulation_out_dn);
            } else {
                circulation_in.setImageResource(R.mipmap.circulation_in_dn);
                circulation_out.setImageResource(R.mipmap.circulation_out_up);
            }
        }

        if(modeRun_fanspeed_last != DevStateValue.fanspeed) {
            if (DevStateValue.fanspeed == 1) {
                fanspeed_low.setImageResource(R.mipmap.fanspeed_low_up);
                fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_dn);
                fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_dn);
            } else if (DevStateValue.fanspeed == 4) {
                fanspeed_low.setImageResource(R.mipmap.fanspeed_low_dn);
                fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_up);
                fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_dn);
            } else {
                fanspeed_low.setImageResource(R.mipmap.fanspeed_low_dn);
                fanspeed_middle.setImageResource(R.mipmap.fanspeed_middle_dn);
                fanspeed_heigth.setImageResource(R.mipmap.fanspeed_heigth_up);
            }
        }

        if(modeRun_coolmode_last != DevStateValue.cool_mode) {
            if (DevStateValue.cool_mode == 1) {
                cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_up);
                cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_dn);
                cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_dn);
            } else if (DevStateValue.cool_mode == 2) {
                cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_dn);
                cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_up);
                cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_dn);
            } else {
                cool_mode_refrigera.setImageResource(R.mipmap.cool_mode_refrigera_dn);
                cool_mode_heat.setImageResource(R.mipmap.cool_mode_heat_dn);
                cool_mode_ventilation.setImageResource(R.mipmap.cool_mode_ventilation_up);
            }
        }

        if(modeRun_coolspeed_last != DevStateValue.cool_speed) {
            if (DevStateValue.cool_speed == 1) {
                cool_speed_low.setImageResource(R.mipmap.cool_speed_low_up);
                cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_dn);
                cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_dn);
            } else if (DevStateValue.cool_speed == 4) {
                cool_speed_low.setImageResource(R.mipmap.cool_speed_low_dn);
                cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_up);
                cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_dn);
            } else {
                cool_speed_low.setImageResource(R.mipmap.cool_speed_low_dn);
                cool_speed_middle.setImageResource(R.mipmap.cool_speed_middle_dn);
                cool_speed_heigth.setImageResource(R.mipmap.cool_speed_heigth_up);
            }
        }
        modeRun_runloop_last =DevStateValue.runloop;
        modeRun_fanspeed_last =DevStateValue.fanspeed;
        modeRun_coolmode_last =DevStateValue.cool_mode;
        modeRun_coolspeed_last =DevStateValue.cool_speed;
    }

    /**
     * 更新界面的传感器UI
     */
    @SuppressLint("SetTextI18n")
    private void updataSensorUI(){

        if(DevStateValue.airGrade ==Config.AIR_GRADE_GOOD)  sensor_air_value.setText("优");
        else if(DevStateValue.airGrade ==Config.AIR_GRADE_LIANG)  sensor_air_value.setText("良");
        else if(DevStateValue.airGrade ==Config.AIR_GRADE_BAD)  sensor_air_value.setText("差");
        else sensor_air_value.setText("—");

        if(DevStateValue.pm25 ==Config.NOTHING) sensor_pm25_value.setText("—");
        else sensor_pm25_value.setText(""+DevStateValue.pm25);

        if(DevStateValue.co2 ==Config.NOTHING) sensor_co2_value.setText("—");
        else sensor_co2_value.setText(""+DevStateValue.co2);

        if(DevStateValue.tvoc ==Config.NOTHING) sensor_tvoc_value.setText("—");
        else sensor_tvoc_value.setText(""+DevStateValue.tvoc);

        if(DevStateValue.temp ==Config.NOTHING) sensor_temp_value.setText("—");
        else sensor_temp_value.setText(""+DevStateValue.temp);

        if(DevStateValue.humidity ==Config.NOTHING) sensor_humidity_value.setText("—");
        else sensor_humidity_value.setText(""+DevStateValue.humidity);

        if(DevStateValue.pm25Grade ==Config.AIR_GRADE_GOOD)  sensor_pm25_image.setImageResource(R.mipmap.airgrade_good);
        else if(DevStateValue.pm25Grade ==Config.AIR_GRADE_LIANG)  sensor_pm25_image.setImageResource(R.mipmap.airgrade_liang);
        else if(DevStateValue.pm25Grade ==Config.AIR_GRADE_BAD)  sensor_pm25_image.setImageResource(R.mipmap.airgrade_bad);
        else sensor_pm25_image.setImageResource(R.mipmap.airgrade_nothing);

        if(DevStateValue.co2Grade ==Config.AIR_GRADE_GOOD)  sensor_co2_image.setImageResource(R.mipmap.airgrade_good);
        else if(DevStateValue.co2Grade ==Config.AIR_GRADE_LIANG)  sensor_co2_image.setImageResource(R.mipmap.airgrade_liang);
        else if(DevStateValue.co2Grade ==Config.AIR_GRADE_BAD)  sensor_co2_image.setImageResource(R.mipmap.airgrade_bad);
        else sensor_co2_image.setImageResource(R.mipmap.airgrade_nothing);

        if(DevStateValue.tvocGrade ==Config.AIR_GRADE_GOOD)  sensor_tvoc_image.setImageResource(R.mipmap.airgrade_good);
        else if(DevStateValue.tvocGrade ==Config.AIR_GRADE_LIANG)  sensor_tvoc_image.setImageResource(R.mipmap.airgrade_liang);
        else if(DevStateValue.tvocGrade ==Config.AIR_GRADE_BAD)  sensor_tvoc_image.setImageResource(R.mipmap.airgrade_bad);
        else sensor_tvoc_image.setImageResource(R.mipmap.airgrade_nothing);

        if(DevStateValue.tempGrade ==Config.AIR_GRADE_GOOD)  sensor_temp_image.setImageResource(R.mipmap.airgrade_good);
        else if(DevStateValue.tempGrade ==Config.AIR_GRADE_LIANG)  sensor_temp_image.setImageResource(R.mipmap.airgrade_liang);
        else if(DevStateValue.tempGrade ==Config.AIR_GRADE_BAD)  sensor_temp_image.setImageResource(R.mipmap.airgrade_bad);
        else sensor_temp_image.setImageResource(R.mipmap.airgrade_nothing);

        if(DevStateValue.humidityGrade ==Config.AIR_GRADE_GOOD)  sensor_humidity_image.setImageResource(R.mipmap.airgrade_good);
        else if(DevStateValue.humidityGrade ==Config.AIR_GRADE_LIANG)  sensor_humidity_image.setImageResource(R.mipmap.airgrade_liang);
        else if(DevStateValue.humidityGrade ==Config.AIR_GRADE_BAD)  sensor_humidity_image.setImageResource(R.mipmap.airgrade_bad);
        else sensor_humidity_image.setImageResource(R.mipmap.airgrade_nothing);
    }

    /**
     * 系统时间启动与逻辑处理
     */
    public void sysTimerStart() {
        if (functionSysTimer == null) {
            functionSysTimer = new Timer();
            functionSysTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    mHandler.sendEmptyMessage(Config.HANDEL_MODE_RUN_RFUNCTION);

                    databaseSaveTimerCount ++;
                    if(databaseSaveTimerCount>=600){
                        databaseSaveTimerCount =0;
                        mHandler.sendEmptyMessage(Config.HANDEL_SAVE_DATABASE);
                    }


                    if (sysTimerCount == 10) {
                        Log.e("测试===", "传感器" + sysTimerCount);
                        mHandler.sendEmptyMessage(Config.HANDEL_SEND_SENSOR);

                    } else if (sysTimerCount >= 15) {
                        Log.e("测试===", "主机" + sysTimerCount);
                        sysTimerCount=5;
                        hostSendType = hostSendType?false:true;
                        if(hostSendType) mHandler.sendEmptyMessage(Config.HANDEL_SEND_HOST_GETSTATE);
                        else mHandler.sendEmptyMessage(Config.HANDEL_SEND_HOST_CONTROL);
                    }
                    sysTimerCount++;
                    //刷新UI必须执行在主线程
                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            airText.setText("定时时间：" + sysTimerCount + "s");
                        }
                    });
                    */
                }
            }, 1000, 1000);
        }
    }

    /**
     * 系统时间停止
     * 备注：非在本界面均停止
     */
    public void sysTimerStop() {
        if (functionSysTimer != null) {
            functionSysTimer.cancel();
            functionSysTimer = null;
        }
    }
}