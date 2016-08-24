package com.molmc.opensdkdemo.base;

import android.app.Application;

import com.molmc.opensdk.openapi.IntoRobotAPI;

/**
 * features:
 * Author：  hhe on 16-7-28 11:29
 * Email：   hhe@molmc.com
 */

public class MyApplication extends Application {

	private String appId = "9fd012a7437774377104cdb0c348a72a";
	private String appKey = "28e3d403affbe11fb37b593cd1fd2eef";

	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//初始化SDK
		IntoRobotAPI.getInstance().initApp(getApplicationContext(), appId, appKey);
		//打印调试信息
		IntoRobotAPI.getInstance().setDebug(true);
	}

	public static MyApplication getInstance(){
		return instance;
	}

}
