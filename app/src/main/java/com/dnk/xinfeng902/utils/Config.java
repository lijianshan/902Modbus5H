package com.dnk.xinfeng902.utils;

public class Config {

    public static final int NOTHING = 0xFFFF;

    public static final int HANDEL_SEND_SENSOR = 0x01;
    public static final int HANDEL_SEND_HOST_CONTROL = 0x02;
    public static final int HANDEL_SEND_HOST_GETSTATE = 0x03;
    public static final int HANDEL_SEND_CONTROL_POWER = 0x04;
    public static final int HANDEL_SEND_CONTROL_FANPOWER = 0x05;
    public static final int HANDEL_SEND_CONTROL_HEAT = 0x06;
    public static final int HANDEL_SEND_CONTROL_FANSPEED  = 0x07;
    public static final int HANDEL_SEND_CONTROL_COOLMODE  = 0x08;
    public static final int HANDEL_SEND_CONTROL_COOLSPEED  = 0x09;
    public static final int HANDEL_SEND_GET_VERSION  = 0x11;
    public static final int HANDEL_UPDATA_CONTROL_UI = 0x21;
    public static final int HANDEL_UPDATA_SENSOR_UI = 0x22;
    public static final int HANDEL_UPDATA_MODERUN_UI = 0x23;
    public static final int HANDEL_MODE_RUN_RFUNCTION = 0x31;
    public static final int HANDEL_SAVE_DATABASE = 0x32;
    public static final int HANDEL_WARNING_STATE= 0x41;


    public static final int MODE_HAND = 2;
    public static final int MODE_SMART = 4;
    public static final int MODE_POWERFUL = 5;


    public static final int COOL_MODE_REFRIGERA = 2;   //制冷
    public static final int COOL_MODE_HOST = 3;        //制热
    public static final int COOL_MODE_VENTILATION = 1; //送风
    public static final int COOL_MODE_DEHUMIDIF = 6;   //除湿
    public static final int COOL_MODE_AUTO = 5;        //自动

    public static final int AIR_GRADE_GOOD =1;
    public static final int AIR_GRADE_LIANG =2;
    public static final int AIR_GRADE_BAD =3;
    public static final int AIR_GRADE_NOTHING =0xFFFF;

    public static final String warninginfo[]={
            "A8","A9","AA","AB","AC","AD","AE","AF",

            "A0   新风电机卡死",
            "A1   回风电机卡死",
            "A2","A3","A4","A5","A6","A7",

            "E8   风机失速故障",
            "E9   线控器通讯故障",
            "EA   ",
            "EB   ",
            "EC   ",
            "ED   ",
            "EE   水位报警故障",
            "EF   ",

            "E0   ",
            "E1   室内外机通讯故障",
            "E2   T1传感器故障",
            "E3   T2传感器故障",
            "E4   T2B传感器故障",
            "E5   室外机故障",
            "E6   过零保护",
            "E7   EEPROM故障",

            "F8   制热T2高温保护",
            "F9   交流过压/欠压保护",
            "H0   外机主板与驱动板通讯故障",
            "H1   ",
            "H2   ",
            "H3   ",
            "H4   30分钟内出现3次P6保护",
            "H5   30分钟内出现3次P2保护",

            "F0   ",
            "F1   ",
            "F2   ",
            "F3   3次过流保护不可恢复",
            "F4   T4故障",
            "F5   ",
            "F6   T3故障",
            "F7   二次测过流保护",

            "P4   排气温度过高保护",
            "P5   T3高温保护",
            "P6   ",
            "P7   ",
            "P8   ",
            "P9   直流风机故障",
            "PA   ",
            "PB   ",

            "H6   100分钟内出现3次P4保护",
            "H7   ",
            "H8   ",
            "H9   10分钟内出现2次P9保护",
            "P0   ",
            "P1   ",
            "P2   ",
            "P3   一次侧过流保护",
    };
}
