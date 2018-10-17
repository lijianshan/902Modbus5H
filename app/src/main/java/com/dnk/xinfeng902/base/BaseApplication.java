package com.dnk.xinfeng902.base;

import android.app.Application;
import android.content.Intent;

import com.dnk.v700.eService;
import com.dnk.xinfeng902.utils.dataBase.DataBaseUtil;
import com.lidroid.xutils.HttpUtils;

import io.realm.Realm;

public class BaseApplication extends Application {

	public static HttpUtils httpUtils;
	public static BaseApplication mAppContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppContext = this;
		httpUtils = new HttpUtils();

		//CrashReport.initCrashReport(getApplicationContext(), "553a75a65c", false);

		//485通讯服务
		Intent intent = new Intent(this, eService.class);
		this.startService(intent);

		//数据库初始化配置
		Realm.init(this);
		DataBaseUtil.checkDatabaseisEmptyAndInit();
	}

	public static BaseApplication getInstance(){
		return mAppContext;
	}
}
