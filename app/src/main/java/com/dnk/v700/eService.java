package com.dnk.v700;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class eService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		//初始化进程通讯事件
		dmsg.start("/exApp");
		//RS485初始化
		vt_uart.start();
		vt_uart.setup(0, 3, 1);  //485 ,9600
		//12V DO关闭
		ioctl.hooter(0);

		System.out.println("start successful!!!!!");
	}
}
