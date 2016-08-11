package com.molmc.opensdkdemo.base;

import android.app.Application;

import com.molmc.opensdk.openapi.IntoRobotAPI;

/**
 * features:
 * Author：  hhe on 16-7-28 11:29
 * Email：   hhe@molmc.com
 */

public class MyApplication extends Application {

	private String appId = "e8554799f71386bb81847b7e97817258";
	private String appKey = "1c7caab5ebfe50556c24d963067327cb";

	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//初始化SDK
		IntoRobotAPI.getInstance().initApp(getApplicationContext(), appId, appKey);
		//打印调试信息
//		IntoRobotAPI.getInstance().setDebug(true);
	}

	public static MyApplication getInstance(){
		return instance;
	}

}
