package com.dnk.xinfeng902.rs455;

import android.os.CountDownTimer;

import com.dnk.v700.vt_uart;
import com.dnk.xinfeng902.utils.Tools;

/**
 * UDP实现类
 *
 * @author Administrator
 */
public class RS485Service {

    //监听485数据接收
    private RS485ConnectListent listenter;

    //重发
    private static String retransmissionHEAD;
    public CountDownTimer dataRetransmission;

    /**
     * 创建485数据接收监听
     */
    public void connectService(RS485ConnectListent listent) {
        listenter = listent;
        vt_uart.setDataLiostener(new vt_uart.OnDataReturnListener() {
            @Override
            public void returnAllDeviceInfoData(byte[] replyData) {

                if (retransmissionHEAD.equals(Tools.changeHexString2(replyData, 0, 2))) {
                    int CRR16check = 0;
                    CRR16check = Tools.modeBusCRC16(replyData, replyData.length - 2);
                    if (((CRR16check >> 8) == (replyData[replyData.length - 2] & 0xff)) && ((CRR16check & 0x00ff) == (replyData[replyData.length - 1] & 0xff))) {

                        listenter.communicationDataReceive(replyData);

                        dataRetransmissionStop();
                    }
                    //Log.e("CRC校验不通过","CRR16check="+CRR16check+";;"+StrHexStrUtils.changeHexString2(replyData, replyData.length-2, 2));
                    //Log.e("超时重发测试",retransmissionHEAD + ";;;" +StrHexStrUtils.changeHexString2(replyData, 0, 2));
                }
            }
        });
    }

    /**
     * 发送485数据
     */
    public boolean sendData(byte[] data) {
        if (data != null && data.length > 4) {
            dataRetransmissionStop();
            dataRetransmissionStart(data);
            return true;
        }
        return false;
    }

    /**
     * 超时重发
     */
    public void dataRetransmissionStart(final byte[] data) {

        retransmissionHEAD = Tools.changeHexString2(data, 0, 2);

        if (dataRetransmission == null) {
            dataRetransmission = new CountDownTimer(3500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    vt_uart.tx(data, data.length);
                    vt_uart.uart_len = 0;
                    if(data[1] == 0x03)
                        vt_uart.targetLength =data[5] * 2 + 5;
                    else if((data[0] == (byte) 0xAA)&&((data[1] == 0x2a)||(data[1] == 0x25)))
                        vt_uart.targetLength =5;
                    else
                        vt_uart.targetLength =8;
                }

                @Override
                public void onFinish() {
                    dataRetransmissionStop();
                }
            };
            dataRetransmission.start();
        }
    }

    /**
     * 停止超时重发
     */
    public void dataRetransmissionStop() {
        if (dataRetransmission != null) {
            dataRetransmission.cancel();
            dataRetransmission = null;
        }
        vt_uart.uart_len = 0;
        vt_uart.targetLength = 0;
    }
}