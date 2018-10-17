package com.dnk.xinfeng902.utils;

public class Config {

    public static final String version =  "1.1.2";


    public static final int NOTHING = 0xFFFF;

    public static final int HANDEL_SEND_SENSOR = 0x01;
    public static final int HANDEL_SEND_HOST_CONTROL = 0x02;
    public static final int HANDEL_SEND_HOST_GETSTATE = 0x03;
    public static final int HANDEL_SEND_CONTROL_POWER = 0x04;
    public static final int HANDEL_SEND_CONTROL_RUNLOOP = 0x05;
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


    public static final int MODE_HAND = 2;
    public static final int MODE_SMART = 4;
    public static final int MODE_POWERFUL = 5;


    public static final int COOL_MODE_REFRIGERA = 1;
    public static final int COOL_MODE_HOST = 2;
    public static final int COOL_MODE_VENTILATION = 0;


    public static final int AIR_GRADE_GOOD =1;
    public static final int AIR_GRADE_LIANG =2;
    public static final int AIR_GRADE_BAD =3;
    public static final int AIR_GRADE_NOTHING =0xFFFF;
}
