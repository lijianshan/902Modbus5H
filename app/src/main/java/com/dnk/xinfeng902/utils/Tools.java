package com.dnk.xinfeng902.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {
    /**
     * 字节数组转字符串
     */
    //弹窗提示
    public static Toast toast = null;

    public static String changeHexString2(byte[] src, int begin, int count) {

        if (src != null) {
            if (src.length >= begin + count) {
                StringBuilder s = new StringBuilder();
                for (int i = begin; i < begin + count; i++) {
                    String hex = Integer.toHexString(src[i] & 0xFF);
                    if (hex.length() == 1) {
                        hex = '0' + hex;
                    }
                    s.append(hex);
                }
                return s.toString();
            }
        }
        return "";
    }
    /**
     * modeBus CRC校验
     */
    public static int modeBusCRC16(byte[] bytes, int length) {

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
//        01-02 01:09:18.840 E/测试===: 传感器10
//        01-02 01:09:18.840 I/485 发送 数据：: 21030033000767f3
//        01-02 01:09:18.890 I/485 接收 数据：: 21030e004501c20000a55a071c003501020a7e
        return ((CRC&0x00ff)<<8)|(CRC>>8);
    }
    /**
     * toast显示  避免重复
     */
    public static void showToast(Context context,String text){
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 获取系统时间
     */
    public static String getSysTime(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return ("Date获取当前日期时间"+simpleDateFormat.format(date));
    }

    /**
     * 获取系统时间的HH:M0格式
     * 例如：01：20     15：50
     */
    public static String getsysHHMM(){

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return formatHHMM(hour,(minute/10)*10);
    }

    @SuppressLint("DefaultLocale")
    public static String formatHHMM(int h, int m){
        return String.format("%02d", h).concat(":").concat(String.format("%02d", m));
    }

    /**
     * 温度float转为2个字节数组格式
     * Math.ceil() -- 返回大于等于数字参数的最小整数(取整函数)，对数字进行上舍入
       Math.floor() -- 返回小于等于数字参数的最大整数，对数字进行下舍入
       Math.round() -- 返回数字最接近的整数，四舍五入
     */
    public static byte[] tempFloat2Int(float temp){

        byte[] data = new byte[2];

        if(temp<0){
            data[0] =0x10;
            temp *=-1;
        }
        temp =(float) (Math.floor(temp * 10)/10.0);
        int zhengshu = (int)temp;
        data[1] =(byte)zhengshu;
        data[0] |=(byte) (Math.round((temp -zhengshu)*10));
        return data;
    }

    /**
     * 获取版本号
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }
}
