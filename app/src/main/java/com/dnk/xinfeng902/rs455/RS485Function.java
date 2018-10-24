package com.dnk.xinfeng902.rs455;

import com.dnk.xinfeng902.utils.Config;
import com.dnk.xinfeng902.utils.DevStateValue;
import com.dnk.xinfeng902.utils.Tools;

public class RS485Function implements RS485ConnectListent {

    //485通信相关
    private RS485Service rs485Service;
    private static int waitCMDType = 0;

    public RS485Function() {
        rs485Service = new RS485Service();
        rs485Service.connectService(this);
    }

    public void send486DataControl(int type) {
        waitCMDType = type;
        switch (type) {

            case Config.HANDEL_SEND_SENSOR:
                send_sensor();
                break;
            case Config.HANDEL_SEND_HOST_GETSTATE:
                send_host_getHostState();
                break;
            case Config.HANDEL_SEND_HOST_CONTROL:
            case Config.HANDEL_SEND_CONTROL_POWER:
            case Config.HANDEL_SEND_CONTROL_FANPOWER:
                send_host_controllState();
                break;
            case Config.HANDEL_SEND_CONTROL_HEAT:
                send_heat();
                break;
            case Config.HANDEL_SEND_CONTROL_FANSPEED:
                send_fanspeed();
                break;
            case Config.HANDEL_SEND_CONTROL_COOLMODE:
                send_coolmode();
                break;
            case Config.HANDEL_SEND_CONTROL_COOLSPEED:
                send_coolspeed();
                break;
            case Config.HANDEL_SEND_GET_VERSION:
                send_getversion();
                break;
        }
    }
    /**
     * 接受到串口数据处理逻辑
     */
    @Override
    public void communicationDataReceive(byte[] data) {
        //注意采集不到数据时，置参数为0xfffe
        int value =0;
        switch (waitCMDType) {
            case Config.HANDEL_SEND_SENSOR:
                if (data.length >= 19) {

                    value =(int) (((data[3] & 0xff) << 8) | data[4] & 0xff);
                    if(value >= 0x8000) {
                        DevStateValue.pm25 =Config.NOTHING;
                        DevStateValue.pm25Grade =Config.AIR_GRADE_NOTHING;
                    } else{
                        DevStateValue.pm25 =value;
                        if(value <35) DevStateValue.pm25Grade =Config.AIR_GRADE_GOOD;
                        else if(value <115) DevStateValue.pm25Grade =Config.AIR_GRADE_LIANG;
                        else  DevStateValue.pm25Grade =Config.AIR_GRADE_BAD;
                    }

                    value =(int) (((data[5] & 0xff) << 8) | data[6] & 0xff);
                    if(value >= 0x8000) {
                        DevStateValue.co2 =Config.NOTHING;
                        DevStateValue.co2Grade =Config.AIR_GRADE_NOTHING;
                    } else{
                        DevStateValue.co2 =value;
                        if(value <995) DevStateValue.co2Grade =Config.AIR_GRADE_GOOD;
                        else if(value <1500) DevStateValue.co2Grade =Config.AIR_GRADE_LIANG;
                        else  DevStateValue.co2Grade =Config.AIR_GRADE_BAD;
                    }

                    value =(int) (((data[7] & 0xff) << 8) | data[8] & 0xff);
                    if(value >= 0x8000) {
                        DevStateValue.tvoc =Config.NOTHING;
                        DevStateValue.tvocGrade =Config.AIR_GRADE_NOTHING;
                    } else{
                        DevStateValue.tvoc =value;
                        if(value <2) DevStateValue.tvocGrade =Config.AIR_GRADE_GOOD;
                        else if(value <31) DevStateValue.tvocGrade =Config.AIR_GRADE_LIANG;
                        else  DevStateValue.tvocGrade =Config.AIR_GRADE_BAD;
                    }

                    if(data[11] == (byte) 0xFF){
                        DevStateValue.temp = (float) (Config.NOTHING);
                        DevStateValue.tempGrade =Config.AIR_GRADE_NOTHING;
                    }else{
                        DevStateValue.temp = (float) ((data[12] & 0xff) + ((data[11] & 0xff) & 0x0f) * 0.1);
                        if (((data[11] & 0xff) & 0xf0) > 0) DevStateValue.temp *= -1;
                        if((DevStateValue.temp >17) && (DevStateValue.temp<25)) DevStateValue.tempGrade =Config.AIR_GRADE_GOOD;
                        else if((DevStateValue.temp <11) || (DevStateValue.temp>28)) DevStateValue.tempGrade =Config.AIR_GRADE_BAD;
                        else  DevStateValue.tempGrade =Config.AIR_GRADE_LIANG;
                    }

                    if(data[13] == (byte) 0xFF){
                        DevStateValue.humidity = (int) (Config.NOTHING);
                        DevStateValue.humidityGrade =Config.AIR_GRADE_NOTHING;
                    }else {
                        DevStateValue.humidity = (int) (((data[13] & 0xff) << 8) + data[14] & 0xff);
                        if((DevStateValue.humidity >39) && (DevStateValue.humidity<59)) DevStateValue.humidityGrade =Config.AIR_GRADE_GOOD;
                        else if((DevStateValue.humidity <30) || (DevStateValue.humidity>80)) DevStateValue.humidityGrade =Config.AIR_GRADE_BAD;
                        else  DevStateValue.humidityGrade =Config.AIR_GRADE_LIANG;
                    }

                    value =(int) (data[16] & 0xff);
                    if(value ==3) DevStateValue.airGrade =Config.AIR_GRADE_GOOD;
                    else if(value ==2) DevStateValue.airGrade =Config.AIR_GRADE_LIANG;
                    else if(value ==1) DevStateValue.airGrade =Config.AIR_GRADE_BAD;
                    else DevStateValue.airGrade =Config.AIR_GRADE_NOTHING;
                }
                receiverDataUpdataUI.receiver_updateUI(data);
                break;

            case Config.HANDEL_SEND_HOST_GETSTATE:
                DevStateValue.hostCooltemp = (int) (((data[3] & 0xff) << 8) | data[4] & 0xff);
                break;

            case Config.HANDEL_SEND_GET_VERSION:
                DevStateValue.version_device  ="V ".concat(Integer.toString(data[3]>>4)).concat(".").concat(Integer.toString(data[3]&0x0f).concat(".").concat(Integer.toString(data[4])));
                break;
        }
    }
    /**
     * 传感器数据采集
     */
    private void send_sensor() {
        sendModbusModel_R(0x21,51, 7);
    }
    /**
     * 设备主机控制状态轮询
     */
    private void send_host_controllState() {
        byte[] data = new byte[22];
        byte[] dataTemp = new byte[2];

        data[1] = (byte) ((DevStateValue.powerswitch) ? (DevStateValue.fanpower?0x01:0x00) : 0x00);
        data[3] = (byte) (DevStateValue.fanspeed);
        data[5] = (byte) (DevStateValue.mode);
        data[7] = (byte) ((DevStateValue.fanpower) ? 0x02 : 0x00);
        data[9] = (byte) ((DevStateValue.heat) ? 0x01 : 0x00);
        data[11] = (byte) ((DevStateValue.powerswitch) ? 0x01 : 0x00);
        data[13] = (byte) (DevStateValue.cool_mode);
        data[15] = (byte) (DevStateValue.cool_speed);

        if (DevStateValue.mode == Config.MODE_HAND)
            data[17] = (byte) (DevStateValue.set_temp_hand);
        else if (DevStateValue.mode == Config.MODE_SMART)
            data[17] = (byte) (DevStateValue.set_temp_smart);
        else
            data[17] = (byte) (DevStateValue.set_temp_powerful);

        data[18] = (byte) 0xA5;
        data[19] = (byte) 0x5A;

        if (DevStateValue.temp == Config.NOTHING){
            data[20] = (byte) 0xff;
            data[21] = (byte) 0xff;
        }else{
            dataTemp = Tools.tempFloat2Int(DevStateValue.temp);
            data[20] = dataTemp[0];
            data[21] = dataTemp[1];
        }

        sendModbusModel_W(0x01,1, data.length, data);
    }
    /**
     * 设备主机状态查询
     */
    private void send_host_getHostState() {
        sendModbusModel_R(0x01,501, 4);
    }
    /**
     * 单控制类指令
     */
    private void send_heat() {
        byte[] data = new byte[2];
        data[1] = (byte) ((DevStateValue.heat) ? 0x01 : 0x00);
        sendModbusModel_W(0x01,5, data.length, data);
    }
    private void send_fanspeed() {
        byte[] data = new byte[2];
        data[1] = (byte) (DevStateValue.fanspeed);
        sendModbusModel_W(0x01,2, data.length, data);
    }
    private void send_coolmode() {
        byte[] data = new byte[2];
        data[1] = (byte) (DevStateValue.cool_mode);
        sendModbusModel_W(0x01,7, data.length, data);
    }
    private void send_coolspeed() {
        byte[] data = new byte[2];
        data[1] = (byte) (DevStateValue.cool_speed);
        sendModbusModel_W(0x01,8, data.length, data);
    }
    /**
     * 获取设备版本号
     */
    private void send_getversion(){
        sendModbusModel_R(0x01,101, 1);
    }
    /**
     * 数据写打包发送
     */
    private void sendModbusModel_W(int devAddr, int addrStart, int dataLength, byte[] data) {

        byte[] senddata = new byte[9 + dataLength];

        senddata[0] = (byte) devAddr;
        senddata[1] = 0x10;

        senddata[2] = (byte) ((addrStart >> 8) & 0xff);
        senddata[3] = (byte) (addrStart & 0xff);

        senddata[4] = (byte) (((dataLength / 2) >> 8) & 0xff);
        senddata[5] = (byte) ((dataLength / 2) & 0xff);

        senddata[6] = (byte) dataLength;


        for (int i = 0; i < dataLength; i++) {
            senddata[7 + i] = data[i];
        }

        int crc16 = Tools.modeBusCRC16(senddata, 7 + dataLength);
        senddata[7 + dataLength] = (byte) ((crc16 >> 8) & 0xff);
        senddata[8 + dataLength] = (byte) (crc16 & 0xff);

        rs485Service.sendData(senddata);
    }

    /**
     * 数据读打包发送
     */
    private void sendModbusModel_R(int devAddr, int addrStart, int addrLength) {

        byte[] senddata = new byte[8];
        senddata[0] = (byte)devAddr;
        senddata[1] = 0x03;

        senddata[2] = (byte) ((addrStart >> 8) & 0xff);
        senddata[3] = (byte) (addrStart & 0xff);

        senddata[4] = (byte) ((addrLength >> 8) & 0xff);
        senddata[5] = (byte) (addrLength & 0xff);

        int crc16 = Tools.modeBusCRC16(senddata, 6);
        senddata[6] = (byte) ((crc16 >> 8) & 0xff);
        senddata[7] = (byte) (crc16 & 0xff);

        //System.out.println(StrHexStrUtils.changeHexString1(senddata));
        rs485Service.sendData(senddata);
    }

    /**
     * 接收到数据去更新UI
     */
    private static receiverDataUpdateUIListener receiverDataUpdataUI;

    public static void receiverDataUpdateUIListenerInit(receiverDataUpdateUIListener updataUIListener) {
        RS485Function.receiverDataUpdataUI = updataUIListener;
    }

    public interface receiverDataUpdateUIListener {
        void receiver_updateUI(byte[] replyData);
    }
}
