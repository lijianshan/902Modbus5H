package com.dnk.xinfeng902.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DataStorage extends Activity {
    /**
     * 控制状态保存到存储区
     */
    public void Data2SharedPreferences(Context context){
        SharedPreferences.Editor editor1 = context.getSharedPreferences("SharedPreferences_control", MODE_PRIVATE).edit();
        editor1.putBoolean("powerswitch", DevStateValue.powerswitch);
        editor1.putInt("mode",DevStateValue.mode);
        editor1.putInt("runloop",DevStateValue.runloop);
        editor1.putBoolean("heat", DevStateValue.heat);
        editor1.putInt("fanspeed",DevStateValue.fanspeed);
        editor1.putInt("cool_mode",DevStateValue.cool_mode);
        editor1.putInt("cool_speed",DevStateValue.cool_speed);

        editor1.putInt("set_temp_hand",DevStateValue.set_temp_hand);
        editor1.putInt("set_temp_smart",DevStateValue.set_temp_smart);
        editor1.putInt("set_temp_powerful",DevStateValue.set_temp_powerful);

        editor1.commit();
    }
    /**
     * 从存储区读取控制状态
     */
    public void SharedPreferences2Data(Context context){
        SharedPreferences sprread = context.getSharedPreferences("SharedPreferences_control", MODE_PRIVATE);
        DevStateValue.powerswitch = sprread.getBoolean("powerswitch",false);
        DevStateValue.mode =sprread.getInt("mode",Config.MODE_HAND);
        DevStateValue.runloop =sprread.getInt("runloop",1);
        DevStateValue.heat = sprread.getBoolean("heat",false);
        DevStateValue.fanspeed =sprread.getInt("fanspeed",1);
        DevStateValue.cool_mode =sprread.getInt("cool_mode",1);
        DevStateValue.cool_speed =sprread.getInt("cool_speed",1);

        DevStateValue.set_temp_hand =sprread.getInt("set_temp_hand",26);
        DevStateValue.set_temp_smart =sprread.getInt("set_temp_smart",26);
        DevStateValue.set_temp_powerful =sprread.getInt("set_temp_powerful",26);
    }
}
