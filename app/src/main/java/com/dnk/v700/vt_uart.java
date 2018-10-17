package com.dnk.v700;

import android.util.Log;

import com.dnk.xinfeng902.utils.Tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class vt_uart {

    public static DatagramSocket uart;
    public static int tPort = 10060;
    public static int rPort = 10062;
    private static ExecutorService service = Executors.newSingleThreadExecutor();

    /**
     * 485接口使能
     */
    public static Boolean start() {
        if (uart != null) {
            uart.close();
        }
        try {
            uart = new DatagramSocket(rPort);

            vt_uart_thread u = new vt_uart_thread();
            service.submit(u);
            //Thread thread = new Thread(u);
            //thread.start();

            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 485接口配置
     */
    public static void setup(int pb, int br, int mode) {
        //pb: 校验位   br: 波特率  mode:串口模式 0:RS232 1:udp
        //pb  0:禁用  1:偶校验  2:奇校验
        //br  0:1200 1:2400 2:4800 3:9600 4:19200 5:38400 6:57600 7:115200
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setInt("/params/parity", pb);
        p.setInt("/params/speed", br);
        p.setInt("/params/mode", mode);
        req.to("/control/vt_uart/setup", p.toString());
    }

    /**
     * 485数据发送
     */
    public static void tx(byte[] data, int length) {
        Log.i("485 发送 数据：", bytesToHexString(data));
        if (uart != null) {
            try {
                DatagramPacket p = new DatagramPacket(data, length, InetAddress.getByName("127.0.0.1"), tPort);
                uart.send(p);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 485数据接收
     */
    public static byte[] uart_rx = new byte[2 * 100];
    public static int uart_len = 0;      //接收到到数据总包数
    public static int targetLength = 0; //数据目标长度接收长度
    public static class vt_uart_thread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    byte[] data = new byte[2 * 100];
                    DatagramPacket p = new DatagramPacket(data, data.length);
                    uart.receive(p);
                    if (p.getLength() > 0 && uart_len + p.getLength() < uart_rx.length && targetLength>4) {
                        System.arraycopy(p.getData(), 0, uart_rx, uart_len, p.getLength());
                        uart_len += p.getLength();
                        //Log.i("485 接收：",   "=uart_len=" +uart_len+  "=targetLength=" +targetLength);
                        if(uart_len>=targetLength){
                            uart_len = 0;
                            Log.i("485 接收 数据：", Tools.changeHexString2(uart_rx,0,targetLength));
                            mDataListener.returnAllDeviceInfoData(Arrays.copyOfRange(uart_rx,0,targetLength));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 字节转16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    private static OnDataReturnListener mDataListener;

    public static void setDataLiostener(OnDataReturnListener mDataListener) {
        vt_uart.mDataListener = mDataListener;
    }

    public interface OnDataReturnListener {
        void returnAllDeviceInfoData(byte[] replyData);

    }
}
